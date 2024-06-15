package filipednb.github.com.hostfullyapi.unit;

import filipednb.github.com.hostfullyapi.domain.booking.BookingStatusEnum;
import filipednb.github.com.hostfullyapi.domain.property.PropertyEntity;
import filipednb.github.com.hostfullyapi.domain.property.PropertyService;
import filipednb.github.com.hostfullyapi.domain.reservation.ReservationService;
import filipednb.github.com.hostfullyapi.domain.booking.BookingEntity;
import filipednb.github.com.hostfullyapi.domain.booking.BookingMapper;
import filipednb.github.com.hostfullyapi.domain.booking.BookingRepository;
import filipednb.github.com.hostfullyapi.domain.booking.BookingRequest;
import filipednb.github.com.hostfullyapi.domain.booking.BookingResponse;
import filipednb.github.com.hostfullyapi.domain.booking.BookingService;
import filipednb.github.com.hostfullyapi.exception.BusinessRuleException;
import filipednb.github.com.hostfullyapi.exception.PropertyBusyException;
import filipednb.github.com.hostfullyapi.exception.ResourceNotFoundException;
import filipednb.github.com.hostfullyapi.domain.user.UserEntity;
import filipednb.github.com.hostfullyapi.domain.user.UserService;
import filipednb.github.com.hostfullyapi.domain.user.UserTypeEnum;
import filipednb.github.com.hostfullyapi.utils.TimeProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private BookingMapper bookingMapper;

    @Mock
    private UserService userService;

    @Mock
    private PropertyService propertyService;

    @Mock
    private ReservationService reservationService;

    private BookingService bookingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        bookingService = new BookingService(bookingMapper,
                userService,
                propertyService,
                bookingRepository,
                reservationService);
        LocalDateTime fixedTime = LocalDateTime.of(2024, 6, 15, 0, 0);
        TimeProvider.setMockTime(fixedTime);
    }

    @Test
    void testCreateBooking() {
        BookingRequest bookingRequest = new BookingRequest();
        bookingRequest.setGuestId(1L);
        bookingRequest.setPropertyId(1L);
        bookingRequest.setCheckInDate(LocalDateTime.of(2024, 6, 15, 0, 0));
        bookingRequest.setCheckOutDate(LocalDateTime.of(2024, 6, 20, 0, 0));

        when(userService.findByIdAndType(eq(1L), any(UserTypeEnum.class)))
                .thenReturn(Optional.of(new UserEntity()));

        PropertyEntity propertyEntity = new PropertyEntity();
        propertyEntity.setId(1L);
        when(propertyService.findEntityById(eq(1L)))
                .thenReturn(propertyEntity);

        when(reservationService.existsBookingOnDate(eq(1L), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(false);

        when(reservationService.isPropertyBlockedOnDate(eq(1L), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(false);

        when(bookingMapper.toResponse(any(BookingEntity.class)))
                .thenReturn(new BookingResponse());

        when(bookingMapper.toEntity(any(BookingRequest.class)))
                .thenReturn(new BookingEntity());

        bookingService.createBooking(bookingRequest);

        verify(reservationService, times(1)).existsBookingOnDate(eq(1L), any(LocalDateTime.class), any(LocalDateTime.class));
        verify(reservationService, times(1)).isPropertyBlockedOnDate(eq(1L), any(LocalDateTime.class), any(LocalDateTime.class));
        verify(bookingMapper, times(1)).toEntity(any(BookingRequest.class));
        verify(bookingMapper, times(1)).toResponse(any(BookingEntity.class));
        verify(bookingRepository, times(1)).save(any(BookingEntity.class));
    }

    @Test
    void testCreateBookingWhenPropertyIsAlreadyBookedAtThatTime() {
        BookingRequest bookingRequest = new BookingRequest();
        bookingRequest.setGuestId(1L);
        bookingRequest.setPropertyId(1L);
        bookingRequest.setCheckInDate(LocalDateTime.of(2024, 6, 15, 0, 0));
        bookingRequest.setCheckOutDate(LocalDateTime.of(2024, 6, 20, 0, 0));

        PropertyEntity propertyEntity = new PropertyEntity();
        propertyEntity.setId(1L);

        when(propertyService.findEntityById(eq(1L)))
                .thenReturn(propertyEntity);

        when(reservationService.existsBookingOnDate(any(Long.class), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(true);

        when(reservationService.isPropertyBlockedOnDate(any(Long.class), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(false);

        when(userService.findByIdAndType(any(Long.class), any(UserTypeEnum.class)))
                .thenReturn(Optional.of(new UserEntity()));

        assertThrows(PropertyBusyException.class, () -> bookingService.createBooking(bookingRequest));
    }

    @Test
    void testCreateBookingWhenPropertyIsBlockedAtThatTime() {
        BookingRequest bookingRequest = new BookingRequest();
        bookingRequest.setGuestId(1L);
        bookingRequest.setPropertyId(1L);
        bookingRequest.setCheckInDate(LocalDateTime.of(2024, 6, 15, 0, 0));
        bookingRequest.setCheckOutDate(LocalDateTime.of(2024, 6, 20, 0, 0));

        PropertyEntity propertyEntity = new PropertyEntity();
        propertyEntity.setId(1L);

        when(propertyService.findEntityById(eq(1L)))
                .thenReturn(propertyEntity);

        when(reservationService.existsBookingOnDate(any(Long.class), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(false);

        when(reservationService.isPropertyBlockedOnDate(any(Long.class), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(true);

        when(userService.findByIdAndType(any(Long.class), any(UserTypeEnum.class)))
                .thenReturn(Optional.of(new UserEntity()));

        assertThrows(PropertyBusyException.class, () -> bookingService.createBooking(bookingRequest));
    }

    @Test
    void testCreateBookingWhenUserIsNotFound() {
        BookingRequest bookingRequest = new BookingRequest();
        bookingRequest.setGuestId(1L);
        bookingRequest.setPropertyId(1L);
        bookingRequest.setCheckInDate(LocalDateTime.of(2024, 6, 15, 0, 0));
        bookingRequest.setCheckOutDate(LocalDateTime.of(2024, 6, 20, 0, 0));

        when(reservationService.existsBookingOnDate(any(Long.class), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(true);

        when(reservationService.isPropertyBlockedOnDate(any(Long.class), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(true);

        when(userService.existsByIdAndType(any(Long.class), any(UserTypeEnum.class)))
                .thenReturn(Optional.of(false));

        assertThrows(ResourceNotFoundException.class, () -> bookingService.createBooking(bookingRequest));
    }

    @Test
    void testCancelBooking() {
        BookingEntity bookingEntity = new BookingEntity();
        bookingEntity.setId(1L);
        bookingEntity.setStatus(BookingStatusEnum.ACTIVE);

        when(bookingRepository.findById(eq(1L))).thenReturn(Optional.of(bookingEntity));
        when(bookingRepository.save(any(BookingEntity.class))).thenReturn(bookingEntity);
        when(bookingMapper.toResponse(any(BookingEntity.class))).thenReturn(new BookingResponse());

        BookingResponse response = bookingService.cancelBooking(1L);

        assertEquals(BookingStatusEnum.CANCELLED, bookingEntity.getStatus());
        verify(bookingRepository, times(1)).save(any(BookingEntity.class));
    }

    @Test
    void testCancelBookingAlreadyCancelled() {
        BookingEntity bookingEntity = new BookingEntity();
        bookingEntity.setId(1L);
        bookingEntity.setStatus(BookingStatusEnum.CANCELLED);

        when(bookingRepository.findById(eq(1L))).thenReturn(Optional.of(bookingEntity));

        assertThrows(BusinessRuleException.class, () -> bookingService.cancelBooking(1L));
    }

    @Test
    void testRebookBooking() {
        BookingEntity bookingEntity = new BookingEntity();
        bookingEntity.setId(1L);
        bookingEntity.setStatus(BookingStatusEnum.CANCELLED);
        UserEntity guest = new UserEntity();
        guest.setId(1L);
        bookingEntity.setGuest(guest);
        bookingEntity.setProperty(new PropertyEntity());

        when(bookingRepository.findById(eq(1L))).thenReturn(Optional.of(bookingEntity));
        when(userService.findByIdAndType(eq(1L), eq(UserTypeEnum.GUEST)))
                .thenReturn(Optional.of(guest));
        when(reservationService.existsBookingOnDate(any(Long.class), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(false);
        when(reservationService.isPropertyBlockedOnDate(any(Long.class), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(false);
        when(bookingRepository.save(any(BookingEntity.class))).thenReturn(bookingEntity);
        when(bookingMapper.toResponse(any(BookingEntity.class))).thenReturn(new BookingResponse());

        BookingResponse response = bookingService.rebookBooking(1L);

        assertEquals(BookingStatusEnum.ACTIVE, bookingEntity.getStatus());
        verify(bookingRepository, times(1)).save(any(BookingEntity.class));
    }


    @Test
    void testRebookBookingAlreadyActive() {
        BookingEntity bookingEntity = new BookingEntity();
        bookingEntity.setId(1L);
        bookingEntity.setStatus(BookingStatusEnum.ACTIVE);

        when(bookingRepository.findById(eq(1L))).thenReturn(Optional.of(bookingEntity));

        assertThrows(BusinessRuleException.class, () -> bookingService.rebookBooking(1L));
    }

    @Test
    void testDeleteBooking() {
        BookingEntity bookingEntity = new BookingEntity();
        bookingEntity.setId(1L);

        when(bookingRepository.findById(eq(1L))).thenReturn(Optional.of(bookingEntity));

        bookingService.deleteBooking(1L);

        verify(bookingRepository, times(1)).delete(bookingEntity);
    }

    @Test
    void testDeleteBookingNotFound() {
        when(bookingRepository.findById(eq(1L))).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookingService.deleteBooking(1L));
    }
}
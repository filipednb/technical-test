package filipednb.github.com.hostfullyapi.unit;

import filipednb.github.com.hostfullyapi.domain.block.BlockEntity;
import filipednb.github.com.hostfullyapi.domain.block.BlockRepository;
import filipednb.github.com.hostfullyapi.domain.booking.BookingEntity;
import filipednb.github.com.hostfullyapi.domain.booking.BookingRepository;
import filipednb.github.com.hostfullyapi.domain.booking.BookingStatusEnum;
import filipednb.github.com.hostfullyapi.domain.property.PropertyEntity;
import filipednb.github.com.hostfullyapi.domain.property.PropertyRepository;
import filipednb.github.com.hostfullyapi.domain.reservation.ReservationService;
import filipednb.github.com.hostfullyapi.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class ReservationServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private BlockRepository blockRepository;

    @Mock
    private PropertyRepository propertyRepository;

    @InjectMocks
    private ReservationService reservationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testIsPropertyBlockedOnDateWithBlocks() {
        when(propertyRepository.findById(any(Long.class))).thenReturn(Optional.of(new PropertyEntity()));
        when(blockRepository.findOneByPropertyIdAndDateRange(any(Long.class), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Collections.singletonList(new BlockEntity()));

        boolean result = reservationService.isPropertyBlockedOnDate(1L, LocalDateTime.now(), LocalDateTime.now().plusDays(1));

        assertTrue(result);
    }

    @Test
    void testIsPropertyBlockedOnDatePropertyNotFound() {
        when(propertyRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> 
            reservationService.isPropertyBlockedOnDate(1L, LocalDateTime.now(), LocalDateTime.now().plusDays(1)));
    }

    @Test
    void testIsPropertyBlockedOnDateNoBlocks() {
        when(propertyRepository.findById(any(Long.class))).thenReturn(Optional.of(new PropertyEntity()));
        when(blockRepository.findOneByPropertyIdAndDateRange(any(Long.class), any(LocalDateTime.class), any(LocalDateTime.class)))
            .thenReturn(Collections.emptyList());

        boolean result = reservationService.isPropertyBlockedOnDate(1L, LocalDateTime.now(), LocalDateTime.now().plusDays(1));

        assertFalse(result);
    }

    @Test
    void testExistsBookingOnDatePropertyNotFound() {
        when(propertyRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> 
            reservationService.existsBookingOnDate(1L, LocalDateTime.now(), LocalDateTime.now().plusDays(1)));
    }

    @Test
    void testExistsBookingOnDateNoBookings() {
        when(propertyRepository.findById(any(Long.class))).thenReturn(Optional.of(new PropertyEntity()));
        when(bookingRepository.findByPropertyIdDateRangeAndStatus(any(Long.class), any(LocalDateTime.class), any(LocalDateTime.class), eq(BookingStatusEnum.ACTIVE)))
            .thenReturn(Collections.emptyList());

        boolean result = reservationService.existsBookingOnDate(1L, LocalDateTime.now(), LocalDateTime.now().plusDays(1));

        assertFalse(result);
    }

    @Test
    void testExistsBookingOnDateWithBookings() {
        when(propertyRepository.findById(any(Long.class))).thenReturn(Optional.of(new PropertyEntity()));
        when(bookingRepository.findByPropertyIdDateRangeAndStatus(any(Long.class), any(LocalDateTime.class), any(LocalDateTime.class), eq(BookingStatusEnum.ACTIVE)))
            .thenReturn(Collections.singletonList(new BookingEntity()));

        boolean result = reservationService.existsBookingOnDate(1L, LocalDateTime.now(), LocalDateTime.now().plusDays(1));

        assertTrue(result);
    }
}
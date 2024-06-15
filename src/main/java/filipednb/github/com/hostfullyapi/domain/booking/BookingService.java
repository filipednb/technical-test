package filipednb.github.com.hostfullyapi.domain.booking;

import filipednb.github.com.hostfullyapi.domain.property.PropertyService;
import filipednb.github.com.hostfullyapi.domain.reservation.ReservationService;
import filipednb.github.com.hostfullyapi.exception.BusinessRuleException;
import filipednb.github.com.hostfullyapi.exception.PropertyBusyException;
import filipednb.github.com.hostfullyapi.exception.ResourceNotFoundException;
import filipednb.github.com.hostfullyapi.domain.user.UserService;
import filipednb.github.com.hostfullyapi.domain.user.UserTypeEnum;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import java.util.stream.Collectors;

import static filipednb.github.com.hostfullyapi.utils.DateUtils.validateDateRange;

@Service
public class BookingService {

    private final BookingMapper mapper;

    private final UserService userService;

    private final PropertyService propertyService;

    private final BookingRepository bookingRepository;

    private final ReservationService reservationService;

    public BookingService(final BookingMapper mapper,
                          final UserService userService,
                          final PropertyService propertyService,
                          final BookingRepository bookingRepository,
                          final ReservationService reservationService) {
        this.mapper = mapper;
        this.userService = userService;
        this.propertyService = propertyService;
        this.bookingRepository = bookingRepository;
        this.reservationService = reservationService;
    }

    @Transactional
    public BookingResponse createBooking(final BookingRequest request) {
        var property = propertyService.findEntityById(request.getPropertyId());
        var checkInDate = request.getCheckInDate();
        var checkOutDate = request.getCheckOutDate();

        // Validates or throw the InvalidDateRangeException
        validateDateRange(checkInDate, checkOutDate, 24);

        var guest = userService.findByIdAndType(request.getGuestId(), UserTypeEnum.GUEST)
                .orElseThrow(() -> new ResourceNotFoundException("Guest not found"));

        if (reservationService.existsBookingOnDate(property.getId(), checkInDate, checkOutDate)) {
            throw new PropertyBusyException("The location is already booked at the given date");
        }

        if (reservationService.isPropertyBlockedOnDate(property.getId(), checkInDate, checkOutDate)) {
            throw new PropertyBusyException("The property was blocked by the owner in the same period");
        }

        BookingEntity entity = mapper.toEntity(request);
        entity.setGuest(guest);
        entity.setProperty(property);
        entity.setCheckInDate(checkInDate);
        entity.setCheckOutDate(checkOutDate);

        bookingRepository.save(entity);

        return mapper.toResponse(entity);
    }

    @Transactional
    public void deleteBooking(final Long id) {
        BookingEntity booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        bookingRepository.delete(booking);
    }

    public List<BookingResponse> findAll() {
        List<BookingEntity> bookings = bookingRepository.findAll();
        return bookings
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    public BookingResponse findById(final Long id) throws ResourceNotFoundException {
        return mapper.toResponse(
                bookingRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Booking not found"))
        );
    }

    @Transactional
    public BookingResponse updateBooking(Long id, BookingRequest bookingRequest) {
        var booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id " + id));
        var property = propertyService.findEntityById(bookingRequest.getPropertyId());
        var checkInDate = bookingRequest.getCheckInDate();
        var checkOutDate = bookingRequest.getCheckOutDate();

        // Validates or throw the InvalidDateRangeException
        validateDateRange(checkInDate, checkOutDate, 24);

        var guest = userService.findByIdAndType(bookingRequest.getGuestId(), UserTypeEnum.GUEST)
                .orElseThrow(() -> new ResourceNotFoundException("Guest not found"));

        if (reservationService.existsBookingOnDate(bookingRequest.getPropertyId(), checkInDate, checkOutDate)) {
            throw new PropertyBusyException("The location is already booked at the given date");
        }

        if (reservationService.isPropertyBlockedOnDate(bookingRequest.getPropertyId(), checkInDate, checkOutDate)) {
            throw new PropertyBusyException("The property was blocked by the owner in the same period");
        }

        booking.setProperty(property);
        booking.setGuest(guest);
        booking.setCheckInDate(checkInDate);
        booking.setCheckOutDate(checkOutDate);

        return mapper.toResponse(bookingRepository.save(booking));
    }

    @Transactional
    public BookingResponse cancelBooking(Long id) {
        var booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id " + id));

        if (booking.getStatus() == BookingStatusEnum.CANCELLED) {
            throw new BusinessRuleException("Booking is already cancelled");
        }

        booking.setStatus(BookingStatusEnum.CANCELLED);

        return mapper.toResponse(bookingRepository.save(booking));
    }

    @Transactional
    public BookingResponse rebookBooking(Long id) {
        BookingEntity booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id " + id));

        if (booking.getStatus() == BookingStatusEnum.ACTIVE) {
            throw new BusinessRuleException("Booking is already booked");
        }

        userService.findByIdAndType(booking.getGuest().getId(), UserTypeEnum.GUEST)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot rebook, user not found"));

        if (reservationService.existsBookingOnDate(booking.getProperty().getId(),
                booking.getCheckInDate(),
                booking.getCheckOutDate())) {
            throw new PropertyBusyException("Cannot rebook, the location is already booked at the given date");
        }

        if (reservationService.isPropertyBlockedOnDate(booking.getProperty().getId(),
                booking.getCheckInDate(),
                booking.getCheckOutDate())) {
            throw new PropertyBusyException("Cannot rebook, the property was blocked by the owner in the same period");
        }

        booking.setStatus(BookingStatusEnum.ACTIVE);

        return mapper.toResponse(bookingRepository.save(booking));
    }
}
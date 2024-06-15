package filipednb.github.com.hostfullyapi.domain.reservation;

import filipednb.github.com.hostfullyapi.domain.block.BlockRepository;
import filipednb.github.com.hostfullyapi.domain.booking.BookingRepository;
import filipednb.github.com.hostfullyapi.domain.booking.BookingStatusEnum;
import filipednb.github.com.hostfullyapi.domain.property.PropertyRepository;
import filipednb.github.com.hostfullyapi.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * In order to avoid circular dependency and weird behaviors between Booking <---> Block
 * I decided to create this unit, that is in charge to use both repositories at same time.
 */
@Service
public class ReservationService {

    private final BookingRepository bookingRepository;

    private final BlockRepository blockRepository;

    private final PropertyRepository propertyRepository;

    ReservationService(final BookingRepository bookingRepository,
                       final BlockRepository blockRepository,
                       final PropertyRepository propertyRepository) {
        this.bookingRepository = bookingRepository;
        this.blockRepository = blockRepository;
        this.propertyRepository = propertyRepository;
    }

    @Transactional
    public boolean isPropertyBlockedOnDate(final Long propertyId, final LocalDateTime startDate, final LocalDateTime endDate) {
        propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));

        var blocks = blockRepository.findOneByPropertyIdAndDateRange(propertyId, startDate, endDate);

        return !blocks.isEmpty();
    }

    @Transactional
    public boolean existsBookingOnDate(final Long propertyId, final LocalDateTime startDate, final LocalDateTime endDate) {
        propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));

        var bookings = bookingRepository.findByPropertyIdDateRangeAndStatus(propertyId,
                startDate, endDate, BookingStatusEnum.ACTIVE);

        return !bookings.isEmpty();
    }
}
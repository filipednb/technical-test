package filipednb.github.com.hostfullyapi.domain.booking;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface BookingRepository extends JpaRepository<BookingEntity, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM BookingEntity b WHERE b.property.id = :propertyId "
            + "AND b.status = :status "
            + "AND (b.checkInDate < :checkOutDate AND b.checkOutDate > :checkInDate)")
    List<BookingEntity> findByPropertyIdDateRangeAndStatus(Long propertyId, LocalDateTime checkInDate, LocalDateTime checkOutDate, BookingStatusEnum status);

    @Modifying
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("UPDATE BookingEntity b SET b.status = :status WHERE b.id = :id")
    void updateBookingStatus(Long id, BookingStatusEnum status);

}

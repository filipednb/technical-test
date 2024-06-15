package filipednb.github.com.hostfullyapi.domain.block;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BlockRepository extends JpaRepository<BlockEntity, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM BlockEntity b WHERE b.property.id = :propertyId "
            + "AND (b.startDate < :endDate AND b.endDate > :startDate)")
    List<BlockEntity> findOneByPropertyIdAndDateRange(Long propertyId, LocalDateTime startDate, LocalDateTime endDate);

}




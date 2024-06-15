package filipednb.github.com.hostfullyapi.domain.property;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

public interface PropertyRepository extends JpaRepository<PropertyEntity, Long> {

    @Override
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<PropertyEntity> findById(Long id);

}

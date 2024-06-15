package filipednb.github.com.hostfullyapi.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    boolean existsByIdAndType(Long id, UserTypeEnum type);

    Optional<UserEntity> findByIdAndType(Long id, UserTypeEnum type);

    boolean existsByEmail(String email);
}

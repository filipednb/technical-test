package filipednb.github.com.hostfullyapi.domain.user;

import filipednb.github.com.hostfullyapi.exception.BadRequestException;
import filipednb.github.com.hostfullyapi.exception.BusinessRuleException;
import filipednb.github.com.hostfullyapi.exception.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import java.util.stream.Collectors;

@Service
@Tag(name = "Users API")
public class UserService {

    private final UserRepository repository;

    private final UserMapper mapper;

    UserService(final UserRepository repository,
                final UserMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<UserResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    public Optional<Boolean> existsByIdAndType(final Long id, final UserTypeEnum type) {
        return Optional.of(this.repository.existsByIdAndType(id, type));
    }

    public Optional<UserEntity> findByIdAndType(final Long id, final  UserTypeEnum type) {
        return repository.findByIdAndType(id, type);
    }

    public UserResponse findById(final Long id) {
        UserEntity entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return mapper.toResponse(entity);
    }

    public UserResponse create(final UserRequest userRequest) {
        UserEntity entity = mapper.toEntity(userRequest);

        repository.save(entity);

        return mapper.toResponse(entity);
    }

    public UserResponse update(final Long id, final UserRequest userRequest) {
        UserEntity entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (entity.getType() != userRequest.getType()) {
            throw new BusinessRuleException("Cannot change user type, create other user instead");
        }

        if (entity.getEmail() == null) {
            throw new BadRequestException("Email is required");
        }

        entity.setEmail(userRequest.getEmail());
        entity.setName(userRequest.getName());

        repository.save(entity);

        return mapper.toResponse(entity);
    }
}

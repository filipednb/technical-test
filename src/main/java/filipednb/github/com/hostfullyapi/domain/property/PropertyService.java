package filipednb.github.com.hostfullyapi.domain.property;

import filipednb.github.com.hostfullyapi.domain.user.UserService;
import filipednb.github.com.hostfullyapi.domain.user.UserTypeEnum;
import filipednb.github.com.hostfullyapi.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PropertyService {

    private final PropertyRepository repository;

    private final PropertyMapper mapper;

    private final UserService userService;

    PropertyService(final PropertyRepository repository,
                    final PropertyMapper mapper,
                    final UserService userService) {
        this.repository = repository;
        this.mapper = mapper;
        this.userService = userService;
    }

    public List<PropertyResponse> findAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    public PropertyResponse findById(@Valid @NotBlank final Long id) {
        return mapper.toResponse(repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found")));
    }

    @Transactional
    public PropertyEntity findEntityById(@Valid @NotBlank final Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));
    }

    public PropertyResponse create(final @Valid PropertyRequest request) {
        var owner = userService.findByIdAndType(request.getOwnerId(), UserTypeEnum.OWNER)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));

        var entity = mapper.toEntity(request);
        entity.setOwner(owner);

        return mapper.toResponse(repository.save(entity));

    }

    @Transactional
    public PropertyResponse update(final Long id, final @Valid PropertyRequest request) {
        var entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));
        var owner = userService.findByIdAndType(request.getOwnerId(), UserTypeEnum.OWNER)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));

        entity.setName(request.getName());
        entity.setLocation(request.getLocation());
        entity.setOwner(owner);

        return mapper.toResponse(repository.save(entity));
    }

}

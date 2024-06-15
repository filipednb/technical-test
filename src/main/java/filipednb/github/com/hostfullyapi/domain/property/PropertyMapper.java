package filipednb.github.com.hostfullyapi.domain.property;

import org.mapstruct.Mapper;
import filipednb.github.com.hostfullyapi.domain.user.UserMapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface PropertyMapper {

    PropertyResponse toResponse(PropertyEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner.id", source = "ownerId")
    PropertyEntity toEntity(PropertyRequest request);
}

package filipednb.github.com.hostfullyapi.domain.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponse toResponse(UserEntity userEntity);

    @Mapping(target = "id", ignore = true)
    UserEntity toEntity(UserRequest userRequest);
}


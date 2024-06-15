package filipednb.github.com.hostfullyapi.domain.block;

import filipednb.github.com.hostfullyapi.domain.property.PropertyMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = { PropertyMapper.class })
public interface BlockMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "property.id", source = "propertyId")
    BlockEntity toEntity(BlockRequest request);

    BlockResponse toResponse(BlockEntity entity);
}
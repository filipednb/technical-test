package filipednb.github.com.hostfullyapi.domain.booking;

import filipednb.github.com.hostfullyapi.domain.property.PropertyMapper;
import filipednb.github.com.hostfullyapi.domain.user.UserMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring", uses = { PropertyMapper.class, UserMapper.class })
public interface BookingMapper {

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "createdAt", ignore = true),
        @Mapping(target = "updatedAt", ignore = true),
        @Mapping(target = "deletedAt", ignore = true),
        @Mapping(target = "status", ignore = true),
        @Mapping(target = "property.id", source = "propertyId"),
        @Mapping(target = "guest.id", source = "guestId")
    })
    BookingEntity toEntity(BookingRequest bookingRequest);

    BookingResponse toResponse(BookingEntity bookingEntity);
}

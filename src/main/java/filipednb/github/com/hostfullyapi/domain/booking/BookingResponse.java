package filipednb.github.com.hostfullyapi.domain.booking;

import filipednb.github.com.hostfullyapi.domain.property.PropertyResponse;
import filipednb.github.com.hostfullyapi.domain.user.UserResponse;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BookingResponse {

    private Long id;

    private UserResponse guest;

    private PropertyResponse property;

    private LocalDateTime checkInDate;

    private LocalDateTime checkOutDate;

    private BookingStatusEnum status;
}
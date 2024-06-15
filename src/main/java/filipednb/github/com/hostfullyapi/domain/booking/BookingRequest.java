package filipednb.github.com.hostfullyapi.domain.booking;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingRequest {
    @NotNull
    private Long guestId;

    @NotNull
    private Long propertyId;

    @NotNull
    @FutureOrPresent(message = "Check-in date must be on present or on future")
    private LocalDateTime checkInDate;

    @NotNull
    @Future(message = "Check-out date must be in the future")
    private LocalDateTime checkOutDate;
}
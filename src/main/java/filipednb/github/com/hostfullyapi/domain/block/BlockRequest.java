package filipednb.github.com.hostfullyapi.domain.block;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BlockRequest {

    @FutureOrPresent (message = "Start date must be on present or on future")
    @NotNull(message = "Start date is mandatory")
    private LocalDateTime startDate;

    @Future(message = "End date must be on future")
    @NotNull (message = "End date is mandatory")
    private LocalDateTime endDate;

    @NotNull(message = "Property ID is mandatory")
    private Long propertyId;

}

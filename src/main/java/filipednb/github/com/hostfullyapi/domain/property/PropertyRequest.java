package filipednb.github.com.hostfullyapi.domain.property;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PropertyRequest {

    @NotBlank(message = "Name is mandatory")
    @Size(min = 5, max = 100, message = "Name must be between 5 and 100 characters")
    private String name;

    @NotBlank(message = "Location is mandatory")
    @Size(min = 5, max = 100, message = "Location must be between 5 and 100 characters")
    private String location;

    @NotNull(message = "Owner ID is mandatory")
    @Min(value = 1, message = "Owner ID must be a positive number")
    private Long ownerId;

}

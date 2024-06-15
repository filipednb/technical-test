package filipednb.github.com.hostfullyapi.domain.user;

import filipednb.github.com.hostfullyapi.validator.EnumValidator;
import filipednb.github.com.hostfullyapi.validator.UniqueEmail;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {

    @NotBlank(message = "Name is mandatory")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 50 characters")
    private String name;

    @UniqueEmail
    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    private String email;

    @NotNull
    @EnumValidator(regexp = "GUEST|OWNER")
    private UserTypeEnum type;

}

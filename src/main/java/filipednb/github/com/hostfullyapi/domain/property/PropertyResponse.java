package filipednb.github.com.hostfullyapi.domain.property;

import filipednb.github.com.hostfullyapi.domain.user.UserResponse;
import lombok.Data;

@Data
public class PropertyResponse {

    private Long id;

    private String name;

    private String location;

    private UserResponse owner;

}

package filipednb.github.com.hostfullyapi.domain.user;

import filipednb.github.com.hostfullyapi.logger.Loggable;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@Loggable
@RestController
@RequestMapping("/users")
@Tag(name = "Users", description = "API for managing users")
public class UserResource {

    private final UserService userService;

    public UserResource(final UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.getAll();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserById(final @Valid @PathVariable Long userId) {
        UserResponse user = userService.findById(userId);

        return ResponseEntity.ok(user);
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(final @Valid @RequestBody UserRequest userRequest) {
        var newUser = userService.create(userRequest);
        var location = URI.create("/users/" + newUser.getId().toString());

        return ResponseEntity.created(location).body(newUser);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<UserResponse> updateUser(final @Valid @PathVariable Long userId,
                                                   final @Valid @RequestBody UserRequest userRequest) {
        UserResponse updatedUser = userService.update(userId, userRequest);
        return ResponseEntity.ok(updatedUser);
    }
}
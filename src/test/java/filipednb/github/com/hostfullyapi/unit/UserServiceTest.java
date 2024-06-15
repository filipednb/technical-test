package filipednb.github.com.hostfullyapi.unit;

import filipednb.github.com.hostfullyapi.domain.user.UserEntity;
import filipednb.github.com.hostfullyapi.domain.user.UserMapper;
import filipednb.github.com.hostfullyapi.domain.user.UserRepository;
import filipednb.github.com.hostfullyapi.domain.user.UserRequest;
import filipednb.github.com.hostfullyapi.domain.user.UserResponse;
import filipednb.github.com.hostfullyapi.domain.user.UserService;
import filipednb.github.com.hostfullyapi.domain.user.UserTypeEnum;
import filipednb.github.com.hostfullyapi.exception.BadRequestException;
import filipednb.github.com.hostfullyapi.exception.BusinessRuleException;
import filipednb.github.com.hostfullyapi.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAll() {
        var userEntity = new UserEntity();
        var userResponse = new UserResponse();

        when(userRepository.findAll()).thenReturn(Collections.singletonList(userEntity));
        when(userMapper.toResponse(any(UserEntity.class))).thenReturn(userResponse);

        var users = userService.getAll();

        assertNotNull(users);
        assertFalse(users.isEmpty());
        verify(userRepository, times(1)).findAll();
        verify(userMapper, times(1)).toResponse(userEntity);
    }

    @Test
    void testExistsByIdAndType() {
        var id = 1L;
        var type = UserTypeEnum.OWNER;

        when(userRepository.existsByIdAndType(id, type)).thenReturn(true);

        var result = userService.existsByIdAndType(id, type);

        assertTrue(result.isPresent());
        assertTrue(result.get());
        verify(userRepository, times(1)).existsByIdAndType(id, type);
    }

    @Test
    void testFindByIdAndType_UserNotFound() {
        var id = 1L;
        var type = UserTypeEnum.OWNER;

        when(userRepository.findByIdAndType(id, type)).thenReturn(Optional.empty());

        var result = userService.findByIdAndType(id, type);

        assertFalse(result.isPresent());
        verify(userRepository, times(1)).findByIdAndType(id, type);
    }

    @Test
    void testFindByIdAndType_Success() {
        var id = 1L;
        var type = UserTypeEnum.OWNER;
        var userEntity = new UserEntity();

        when(userRepository.findByIdAndType(id, type)).thenReturn(Optional.of(userEntity));

        var result = userService.findByIdAndType(id, type);

        assertTrue(result.isPresent());
        assertEquals(userEntity, result.get());
        verify(userRepository, times(1)).findByIdAndType(id, type);
    }

    @Test
    void testFindById_UserNotFound() {
        var id = 1L;

        when(userRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.findById(id));
    }

    @Test
    void testFindById_Success() {
        var id = 1L;
        var userEntity = new UserEntity();
        var userResponse = new UserResponse();

        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(userEntity));
        when(userMapper.toResponse(any(UserEntity.class))).thenReturn(userResponse);

        var response = userService.findById(id);

        assertNotNull(response);
        verify(userRepository, times(1)).findById(id);
        verify(userMapper, times(1)).toResponse(userEntity);
    }

    @Test
    void testCreateUser_Success() {
        var userRequest = new UserRequest();
        var userEntity = new UserEntity();
        var userResponse = new UserResponse();

        when(userMapper.toEntity(any(UserRequest.class))).thenReturn(userEntity);
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
        when(userMapper.toResponse(any(UserEntity.class))).thenReturn(userResponse);

        var response = userService.create(userRequest);

        assertNotNull(response);
        verify(userMapper, times(1)).toEntity(userRequest);
        verify(userRepository, times(1)).save(userEntity);
        verify(userMapper, times(1)).toResponse(userEntity);
    }

    @Test
    void testUpdateUser_UserNotFound() {
        var id = 1L;
        var userRequest = new UserRequest();

        when(userRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.update(id, userRequest));
    }

    @Test
    void testUpdateUser_ChangeUserType() {
        var id = 1L;
        var userRequest = new UserRequest();
        userRequest.setType(UserTypeEnum.GUEST);

        var userEntity = new UserEntity();
        userEntity.setType(UserTypeEnum.OWNER);

        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(userEntity));

        assertThrows(BusinessRuleException.class, () -> userService.update(id, userRequest));
    }

    @Test
    void testUpdateUser_EmailRequired() {
        var id = 1L;
        var userRequest = new UserRequest();
        userRequest.setEmail(null);
        userRequest.setType(UserTypeEnum.OWNER);
        userRequest.setName("Jonah Clement");

        var userEntity = new UserEntity();
        userEntity.setType(UserTypeEnum.OWNER);

        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(userEntity));

        assertThrows(BadRequestException.class, () -> userService.update(id, userRequest));
    }

    @Test
    void testUpdateUser_Success() {
        var id = 1L;
        var userRequest = new UserRequest();
        userRequest.setType(UserTypeEnum.OWNER);
        userRequest.setEmail("test@example.com");
        userRequest.setName("Test User");

        var userEntity = new UserEntity();
        userEntity.setType(UserTypeEnum.OWNER);
        userEntity.setEmail("old@example.com");
        userEntity.setName("Old Name");

        var userResponse = new UserResponse();

        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(userEntity));
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
        when(userMapper.toResponse(any(UserEntity.class))).thenReturn(userResponse);

        var response = userService.update(id, userRequest);

        assertNotNull(response);
        assertEquals(userRequest.getEmail(), userEntity.getEmail());
        assertEquals(userRequest.getName(), userEntity.getName());
        verify(userRepository, times(1)).findById(id);
        verify(userRepository, times(1)).save(userEntity);
        verify(userMapper, times(1)).toResponse(userEntity);
    }
}
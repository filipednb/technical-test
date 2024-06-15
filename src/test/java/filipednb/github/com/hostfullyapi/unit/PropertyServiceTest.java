package filipednb.github.com.hostfullyapi.unit;

import filipednb.github.com.hostfullyapi.domain.property.PropertyEntity;
import filipednb.github.com.hostfullyapi.domain.property.PropertyMapper;
import filipednb.github.com.hostfullyapi.domain.property.PropertyRepository;
import filipednb.github.com.hostfullyapi.domain.property.PropertyRequest;
import filipednb.github.com.hostfullyapi.domain.property.PropertyResponse;
import filipednb.github.com.hostfullyapi.domain.property.PropertyService;
import filipednb.github.com.hostfullyapi.domain.user.UserEntity;
import filipednb.github.com.hostfullyapi.domain.user.UserService;
import filipednb.github.com.hostfullyapi.domain.user.UserTypeEnum;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class PropertyServiceTest {

    @Mock
    private PropertyRepository propertyRepository;

    @Mock
    private PropertyMapper propertyMapper;

    @Mock
    private UserService userService;

    @InjectMocks
    private PropertyService propertyService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAll() {
        var propertyEntity = new PropertyEntity();
        var propertyResponse = new PropertyResponse();

        when(propertyRepository.findAll()).thenReturn(Collections.singletonList(propertyEntity));
        when(propertyMapper.toResponse(any(PropertyEntity.class))).thenReturn(propertyResponse);

        var properties = propertyService.findAll();

        assertNotNull(properties);
        assertFalse(properties.isEmpty());
        verify(propertyRepository, times(1)).findAll();
        verify(propertyMapper, times(1)).toResponse(propertyEntity);
    }

    @Test
    void testFindById_PropertyNotFound() {
        when(propertyRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> propertyService.findById(1L));
    }

    @Test
    void testFindById_Success() {
        var propertyEntity = new PropertyEntity();
        var propertyResponse = new PropertyResponse();

        when(propertyRepository.findById(any(Long.class))).thenReturn(Optional.of(propertyEntity));
        when(propertyMapper.toResponse(any(PropertyEntity.class))).thenReturn(propertyResponse);

        var response = propertyService.findById(1L);

        assertNotNull(response);
        verify(propertyRepository, times(1)).findById(1L);
        verify(propertyMapper, times(1)).toResponse(propertyEntity);
    }

    @Test
    void testCreateProperty_OwnerNotFound() {
        var request = new PropertyRequest();
        request.setOwnerId(1L);

        when(userService.findByIdAndType(any(Long.class), eq(UserTypeEnum.OWNER))).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> propertyService.create(request));
    }

    @Test
    void testCreateProperty_Success() {
        var request = new PropertyRequest();
        request.setOwnerId(1L);

        var owner = new UserEntity();
        var propertyEntity = new PropertyEntity();
        var propertyResponse = new PropertyResponse();

        when(userService.findByIdAndType(any(Long.class), eq(UserTypeEnum.OWNER))).thenReturn(Optional.of(owner));
        when(propertyMapper.toEntity(any(PropertyRequest.class))).thenReturn(propertyEntity);
        when(propertyRepository.save(any(PropertyEntity.class))).thenReturn(propertyEntity);
        when(propertyMapper.toResponse(any(PropertyEntity.class))).thenReturn(propertyResponse);

        var response = propertyService.create(request);

        assertNotNull(response);
        verify(userService, times(1)).findByIdAndType(1L, UserTypeEnum.OWNER);
        verify(propertyMapper, times(1)).toEntity(request);
        verify(propertyRepository, times(1)).save(propertyEntity);
        verify(propertyMapper, times(1)).toResponse(propertyEntity);
    }

    @Test
    void testUpdateProperty_PropertyNotFound() {
        var request = new PropertyRequest();
        var id = 1L;

        when(propertyRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> propertyService.update(id, request));
    }

    @Test
    void testUpdateProperty_OwnerNotFound() {
        var request = new PropertyRequest();
        var id = 1L;

        var propertyEntity = new PropertyEntity();

        when(propertyRepository.findById(any(Long.class))).thenReturn(Optional.of(propertyEntity));
        when(userService.findByIdAndType(any(Long.class), eq(UserTypeEnum.OWNER))).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> propertyService.update(id, request));
    }

    @Test
    void testUpdateProperty_Success() {
        var request = new PropertyRequest();
        request.setOwnerId(1L);
        var id = 1L;

        var propertyEntity = new PropertyEntity();
        var owner = new UserEntity();
        var propertyResponse = new PropertyResponse();

        when(propertyRepository.findById(any(Long.class))).thenReturn(Optional.of(propertyEntity));
        when(userService.findByIdAndType(any(Long.class), eq(UserTypeEnum.OWNER))).thenReturn(Optional.of(owner));
        when(propertyRepository.save(any(PropertyEntity.class))).thenReturn(propertyEntity);
        when(propertyMapper.toResponse(any(PropertyEntity.class))).thenReturn(propertyResponse);

        var response = propertyService.update(id, request);

        assertNotNull(response);
        verify(propertyRepository, times(1)).findById(id);
        verify(userService, times(1)).findByIdAndType(1L, UserTypeEnum.OWNER);
        verify(propertyRepository, times(1)).save(propertyEntity);
        verify(propertyMapper, times(1)).toResponse(propertyEntity);
    }
}
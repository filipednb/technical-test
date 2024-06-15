package filipednb.github.com.hostfullyapi.unit;

import filipednb.github.com.hostfullyapi.domain.block.BlockEntity;
import filipednb.github.com.hostfullyapi.domain.block.BlockMapper;
import filipednb.github.com.hostfullyapi.domain.block.BlockRepository;
import filipednb.github.com.hostfullyapi.domain.block.BlockRequest;
import filipednb.github.com.hostfullyapi.domain.block.BlockResponse;
import filipednb.github.com.hostfullyapi.domain.block.BlockService;
import filipednb.github.com.hostfullyapi.domain.property.PropertyEntity;
import filipednb.github.com.hostfullyapi.domain.property.PropertyService;
import filipednb.github.com.hostfullyapi.domain.reservation.ReservationService;
import filipednb.github.com.hostfullyapi.exception.PropertyBlockedException;
import filipednb.github.com.hostfullyapi.exception.PropertyBusyException;
import filipednb.github.com.hostfullyapi.exception.ResourceNotFoundException;
import filipednb.github.com.hostfullyapi.utils.TimeProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BlockServiceTest {

    @Mock
    private BlockRepository blockRepository;

    @Mock
    private ReservationService reservationService;

    @Mock
    private PropertyService propertyService;

    @Mock
    private BlockMapper blockMapper;

    @InjectMocks
    private BlockService blockService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        LocalDateTime fixedTime = LocalDateTime.of(2024, 6, 15, 0, 0);
        TimeProvider.setMockTime(fixedTime);
    }

    @Test
    void testCreateBlock_PropertyNotFound() {
        var request = new BlockRequest();
        request.setPropertyId(1L);
        request.setStartDate(LocalDateTime.now());
        request.setEndDate(LocalDateTime.now().plusDays(1));

        when(propertyService.findEntityById(any(Long.class))).thenThrow(new ResourceNotFoundException("Property not found"));

        assertThrows(ResourceNotFoundException.class, () ->
                blockService.createBlock(request));
    }

    @Test
    void testCreateBlock_PropertyBlocked() {
        var request = new BlockRequest();
        request.setPropertyId(1L);
        request.setStartDate(LocalDateTime.now());
        request.setEndDate(LocalDateTime.now().plusDays(1));

        PropertyEntity propertyEntity = new PropertyEntity();
        propertyEntity.setId(1L);

        when(propertyService.findEntityById(any(Long.class))).thenReturn(propertyEntity);
        when(reservationService.isPropertyBlockedOnDate(any(Long.class), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(true);

        assertThrows(PropertyBlockedException.class, () ->
                blockService.createBlock(request));
    }

    @Test
    void testCreateBlock_PropertyBusy() {
        var request = new BlockRequest();
        request.setPropertyId(1L);
        request.setStartDate(LocalDateTime.now());
        request.setEndDate(LocalDateTime.now().plusDays(1));

        PropertyEntity propertyEntity = new PropertyEntity();
        propertyEntity.setId(1L);

        when(propertyService.findEntityById(any(Long.class))).thenReturn(propertyEntity);
        when(reservationService.isPropertyBlockedOnDate(any(Long.class), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(false);
        when(reservationService.existsBookingOnDate(any(Long.class), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(true);

        assertThrows(PropertyBusyException.class, () ->
                blockService.createBlock(request));
    }

    @Test
    void testCreateBlock_Success() {
        var request = new BlockRequest();
        request.setPropertyId(1L);
        request.setStartDate(LocalDateTime.now());
        request.setEndDate(LocalDateTime.now().plusDays(1));

        PropertyEntity propertyEntity = new PropertyEntity();
        propertyEntity.setId(1L);

        when(propertyService.findEntityById(any(Long.class))).thenReturn(propertyEntity);
        when(reservationService.isPropertyBlockedOnDate(any(Long.class), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(false);
        when(reservationService.existsBookingOnDate(any(Long.class), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(false);
        when(blockRepository.save(any(BlockEntity.class))).thenReturn(new BlockEntity());
        when(blockMapper.toResponse(any(BlockEntity.class))).thenReturn(new BlockResponse());

        BlockResponse response = blockService.createBlock(request);

        assertNotNull(response);
        verify(blockRepository, times(1)).save(any(BlockEntity.class));
    }

    @Test
    void testUpdateBlock_BlockNotFound() {
        var blockDetails = new BlockRequest();
        blockDetails.setStartDate(LocalDateTime.now());
        blockDetails.setEndDate(LocalDateTime.now().plusDays(1));
        blockDetails.setPropertyId(1L);

        when(blockRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                blockService.updateBlock(1L, blockDetails));
    }

    @Test
    void testUpdateBlock_PropertyBusy() {
        var blockRequest = new BlockRequest();
        blockRequest.setStartDate(LocalDateTime.now().plusHours(1));
        blockRequest.setEndDate(LocalDateTime.now().plusDays(1));
        PropertyEntity propertyEntity = new PropertyEntity();
        propertyEntity.setId(1L);
        blockRequest.setPropertyId(1L);

        BlockEntity existingBlock = new BlockEntity();
        existingBlock.setId(1L);
        existingBlock.setProperty(propertyEntity);

        when(blockRepository.findById(any(Long.class))).thenReturn(Optional.of(existingBlock));
        when(reservationService.isPropertyBlockedOnDate(any(Long.class), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(false);
        when(reservationService.existsBookingOnDate(any(Long.class), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(true);

        assertThrows(PropertyBusyException.class, () -> blockService.updateBlock(1L, blockRequest));
    }

    @Test
    void testUpdateBlock_Success() {
        var blockRequest = new BlockRequest();

        blockRequest.setStartDate(LocalDateTime.now());
        blockRequest.setEndDate(LocalDateTime.now().plusDays(1));
        PropertyEntity propertyEntity = new PropertyEntity();
        propertyEntity.setId(1L);
        blockRequest.setPropertyId(1L);

        BlockEntity existingBlock = new BlockEntity();
        existingBlock.setId(1L);

        when(blockRepository.findById(any(Long.class))).thenReturn(Optional.of(existingBlock));
        when(reservationService.isPropertyBlockedOnDate(any(Long.class), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(false);
        when(reservationService.existsBookingOnDate(any(Long.class), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(false);
        when(blockRepository.save(any(BlockEntity.class))).thenReturn(existingBlock);

        var updatedBlock = blockService.updateBlock(1L, blockRequest);

        assertNotNull(updatedBlock);
        assertEquals(blockRequest.getStartDate(), updatedBlock.getStartDate());
        assertEquals(blockRequest.getEndDate(), updatedBlock.getEndDate());
        verify(blockRepository, times(1)).save(any(BlockEntity.class));
    }

    @Test
    void testDeleteBlockBlockNotFound() {
        when(blockRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> blockService.deleteBlock(1L));
    }

    @Test
    void testDeleteBlockSuccess() {
        BlockEntity blockEntity = new BlockEntity();
        blockEntity.setId(1L);

        when(blockRepository.findById(any(Long.class))).thenReturn(Optional.of(blockEntity));

        blockService.deleteBlock(1L);

        verify(blockRepository, times(1)).delete(blockEntity);
    }

    @Test
    void testGetBlockBlockNotFound() {
        when(blockRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> blockService.getBlock(1L));
    }

    @Test
    void testGetBlockSuccess() {
        BlockEntity blockEntity = new BlockEntity();
        blockEntity.setId(1L);

        when(blockRepository.findById(any(Long.class))).thenReturn(Optional.of(blockEntity));
        when(blockMapper.toResponse(any(BlockEntity.class))).thenReturn(new BlockResponse());

        BlockResponse response = blockService.getBlock(1L);

        assertNotNull(response);
    }
}
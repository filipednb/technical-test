package filipednb.github.com.hostfullyapi.domain.block;

import filipednb.github.com.hostfullyapi.domain.reservation.ReservationService;
import filipednb.github.com.hostfullyapi.domain.property.PropertyService;
import filipednb.github.com.hostfullyapi.exception.PropertyBlockedException;
import filipednb.github.com.hostfullyapi.exception.PropertyBusyException;
import filipednb.github.com.hostfullyapi.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static filipednb.github.com.hostfullyapi.utils.DateUtils.validateDateRange;

@Service
public class BlockService {

    private static final int MIN_HOURS_DURATION = 24;

    private final BlockRepository repository;

    private final ReservationService reservationService;

    private final PropertyService propertyService;

    private final BlockMapper mapper;

    BlockService(final BlockRepository repository,
                 final ReservationService reservationService,
                 final PropertyService propertyService,
                 final BlockMapper mapper) {
        this.repository = repository;
        this.reservationService = reservationService;
        this.propertyService = propertyService;
        this.mapper = mapper;
    }

    @Transactional
    public BlockResponse createBlock(final BlockRequest request) {
        var property = propertyService.findEntityById(request.getPropertyId());

        LocalDateTime startDate = request.getStartDate();
        LocalDateTime endDate = request.getEndDate();

        // Validates or throw the InvalidDateRangeException
        validateDateRange(startDate, endDate, 24);

        // Property busy validation
        if (reservationService.isPropertyBlockedOnDate(property.getId(), startDate, endDate)) {
            throw new PropertyBlockedException("Property is already blocked on this date range");
        }

        if (reservationService.existsBookingOnDate(property.getId(), startDate, endDate)) {
            throw new PropertyBusyException("Property is already booked on this date range");
        }

        var block = new BlockEntity();
        block.setProperty(property);
        block.setStartDate(startDate);
        block.setEndDate(endDate);

        return mapper.toResponse(repository.save(block));
    }

    @Transactional
    public BlockEntity updateBlock(final Long id, final BlockRequest blockRequest) {
        LocalDateTime endDate = blockRequest.getEndDate();
        LocalDateTime startDate = blockRequest.getStartDate();
        Long propertyId = blockRequest.getPropertyId();

        var block = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Block not found with id " + id));

        var property = propertyService.findEntityById(propertyId);

        if (reservationService.isPropertyBlockedOnDate(propertyId, startDate, endDate)) {
            throw new PropertyBusyException("Property is already blocked on this date range");
        }

        if (reservationService.existsBookingOnDate(propertyId, startDate, endDate)) {
            throw new PropertyBusyException("Property is already booked on this date range");
        }

        block.setProperty(property);
        block.setEndDate(blockRequest.getEndDate());
        block.setStartDate(blockRequest.getStartDate());

        return repository.save(block);
    }

    @Transactional
    public void deleteBlock(final Long id) {
        var block = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Block not found with id " + id));

        repository.delete(block);
    }

    public BlockResponse getBlock(final Long id) {
        var entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Block not found with id " + id));

        return mapper.toResponse(entity);
    }

    public List<BlockEntity> getAllBlocks() {
        return repository.findAll();
    }
}
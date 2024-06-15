package filipednb.github.com.hostfullyapi.domain.block;

import filipednb.github.com.hostfullyapi.logger.Loggable;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@Loggable
@RestController
@RequestMapping("/blocks")
@Tag(name = "Blocks", description = "API for managing Property blocks")
public class BlockResource {

    private final BlockService service;

    private final BlockMapper mapper;

    BlockResource(final BlockService service,
                  final BlockMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    public List<BlockResponse> getAllBlocks() {
        return service.getAllBlocks()
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BlockResponse> getBlockById(final @Valid @PathVariable Long id) {
        var block = service.getBlock(id);

        return ResponseEntity.ok(block);
    }

    @PostMapping
    public ResponseEntity<BlockResponse> createBlock(final @Valid @RequestBody BlockRequest block) {
        var createdBlock = service.createBlock(block);
        var uriLocation = URI.create("/blocks/" + createdBlock.getId());

        return ResponseEntity.created(uriLocation).body(createdBlock);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<BlockResponse> updateBlock(final @PathVariable Long id, final @RequestBody BlockRequest request) {
        var updatedBlock = service.updateBlock(id, request);

        return ResponseEntity.ok(mapper.toResponse(updatedBlock));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBlock(final @PathVariable Long id) {
        service.deleteBlock(id);

        return ResponseEntity.noContent().build();
    }
}
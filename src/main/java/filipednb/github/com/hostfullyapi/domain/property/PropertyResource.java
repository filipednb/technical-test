package filipednb.github.com.hostfullyapi.domain.property;

import filipednb.github.com.hostfullyapi.logger.Loggable;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@Loggable
@RestController
@RequestMapping("/properties")
@Tag(name = "Properties", description = "API to manage properties")
public class PropertyResource {

    private final PropertyService service;

    public PropertyResource(final PropertyService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<PropertyResponse>> getAllProperties() {
        var properties = service.findAll();

        return ResponseEntity.ok(properties);
    }

    @GetMapping("/{propertyId}")
    public ResponseEntity<PropertyResponse> getPropertyById(final @PathVariable Long propertyId) {
        var property = service.findById(propertyId);

        return ResponseEntity.ok(property);
    }

    @PostMapping
    public ResponseEntity<PropertyResponse> createProperty(final @Valid @RequestBody PropertyRequest propertyRequest) {
        var newProperty = service.create(propertyRequest);
        URI location = URI.create("/properties/" + newProperty.getId().toString());

        return ResponseEntity.created(location).body(newProperty);
    }

    @PatchMapping("/{propertyId}")
    public ResponseEntity<PropertyResponse> updateProperty(final @PathVariable Long propertyId,
                                                           final @Valid @RequestBody PropertyRequest propertyRequest) {
        PropertyResponse updatedProperty = service.update(propertyId, propertyRequest);
        return ResponseEntity.ok(updatedProperty);
    }
}
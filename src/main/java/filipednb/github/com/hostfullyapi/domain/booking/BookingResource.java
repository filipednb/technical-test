package filipednb.github.com.hostfullyapi.domain.booking;

import filipednb.github.com.hostfullyapi.logger.Loggable;
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

@Loggable
@RestController
@RequestMapping("/bookings")
@Tag(name = "Bookings", description = "API for managing Bookings")
public class BookingResource {

    private final BookingService service;

    public BookingResource(final BookingService service) {
        this.service = service;
    }

    @GetMapping
    public List<BookingResponse> getBookings() {
        return service.findAll();
    }

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(final @RequestBody BookingRequest booking) {
        var createdBooking = service.createBooking(booking);
        var location = URI.create("/bookings/" + createdBooking.getId().toString());

        return ResponseEntity.created(location).body(createdBooking);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse> getBookingById(final @PathVariable Long id) {
        var booking = service.findById(id);

        return ResponseEntity.ok(booking);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<BookingResponse> updateBooking(final @Valid @PathVariable Long id, final @Valid @RequestBody BookingRequest updatedBooking) {
        var response = service.updateBooking(id, updatedBooking);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<BookingResponse> cancelBooking(final @Valid @PathVariable Long id) {
        var response = service.cancelBooking(id);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/rebook")
    public ResponseEntity<BookingResponse> rebookBooking(final @Valid @PathVariable Long id) {
        var response = service.rebookBooking(id);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBooking(final @Valid @PathVariable Long id) {
        service.deleteBooking(id);

        return ResponseEntity.noContent().build();
    }
}


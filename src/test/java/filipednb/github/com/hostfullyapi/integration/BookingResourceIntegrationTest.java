package filipednb.github.com.hostfullyapi.integration;

import filipednb.github.com.hostfullyapi.HostfullyapiApplication;
import filipednb.github.com.hostfullyapi.domain.block.BlockRepository;
import filipednb.github.com.hostfullyapi.domain.booking.BookingEntity;
import filipednb.github.com.hostfullyapi.domain.booking.BookingRepository;
import filipednb.github.com.hostfullyapi.domain.booking.BookingRequest;
import filipednb.github.com.hostfullyapi.domain.booking.BookingStatusEnum;
import filipednb.github.com.hostfullyapi.domain.property.PropertyEntity;
import filipednb.github.com.hostfullyapi.domain.property.PropertyRepository;
import filipednb.github.com.hostfullyapi.domain.user.UserEntity;
import filipednb.github.com.hostfullyapi.domain.user.UserRepository;
import filipednb.github.com.hostfullyapi.domain.user.UserTypeEnum;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest(classes = HostfullyapiApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class BookingResourceIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BlockRepository blockRepository;

    private PropertyEntity testProperty;
    private UserEntity testGuest;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        bookingRepository.deleteAll();
        propertyRepository.deleteAll();
        userRepository.deleteAll();

        // Create and save a test owner
        var owner = new UserEntity();
        owner.setType(UserTypeEnum.OWNER);
        owner.setName("Jack Spencer");
        owner.setEmail("jack.spec@dummy.com");
        owner = userRepository.save(owner);

        // Create and save a test property
        testProperty = new PropertyEntity();
        testProperty.setName("Ocean View Apartment");
        testProperty.setOwner(owner);
        testProperty.setLocation("Miami, FL");
        testProperty = propertyRepository.save(testProperty);

        // Create and save a test guest
        testGuest = new UserEntity();
        testGuest.setType(UserTypeEnum.GUEST);
        testGuest.setName("Jonah Clement");
        testGuest.setEmail("jonah.cle@yahoo.com");
        testGuest = userRepository.save(testGuest);
    }

    @AfterEach
    void cleanUp() {
        bookingRepository.deleteAll();
        blockRepository.deleteAll();
        propertyRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testGetAllBookings() {
        var bookingEntity = new BookingEntity();
        bookingEntity.setCheckInDate(LocalDateTime.now().plusDays(1));
        bookingEntity.setCheckOutDate(LocalDateTime.now().plusDays(2));
        bookingEntity.setProperty(testProperty);
        bookingEntity.setGuest(testGuest);
        bookingRepository.save(bookingEntity);

        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/bookings")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("size()", is(1))
                .body("[0].id", notNullValue());
    }

    @Test
    void testGetBookingById() {
        var bookingEntity = new BookingEntity();
        bookingEntity.setCheckInDate(LocalDateTime.now().plusDays(1));
        bookingEntity.setCheckOutDate(LocalDateTime.now().plusDays(2));
        bookingEntity.setProperty(testProperty);
        bookingEntity.setGuest(testGuest);
        var savedBooking = bookingRepository.save(bookingEntity);

        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/bookings/{id}", savedBooking.getId())
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", is(savedBooking.getId().intValue()))
                .body("checkInDate", notNullValue())
                .body("checkOutDate", notNullValue());
    }

    @Test
    void testCreateBooking() {
        var bookingRequest = new BookingRequest();
        bookingRequest.setCheckInDate(LocalDateTime.now().plusDays(1));
        bookingRequest.setCheckOutDate(LocalDateTime.now().plusDays(2));
        bookingRequest.setPropertyId(testProperty.getId());
        bookingRequest.setGuestId(testGuest.getId());

        given()
                .contentType(ContentType.JSON)
                .body(bookingRequest)
                .when()
                .post("/bookings")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .header("Location", containsString("/bookings/"))
                .body("id", notNullValue())
                .body("checkInDate", notNullValue())
                .body("checkOutDate", notNullValue());
    }

    @Test
    void testUpdateBooking() {
        var bookingEntity = new BookingEntity();
        bookingEntity.setCheckInDate(LocalDateTime.now().plusDays(1));
        bookingEntity.setCheckOutDate(LocalDateTime.now().plusDays(2));
        bookingEntity.setProperty(testProperty);
        bookingEntity.setGuest(testGuest);
        var savedBooking = bookingRepository.save(bookingEntity);

        var updateRequest = new BookingRequest();
        updateRequest.setCheckInDate(LocalDateTime.now().plusDays(3));
        updateRequest.setCheckOutDate(LocalDateTime.now().plusDays(4));
        updateRequest.setPropertyId(testProperty.getId());
        updateRequest.setGuestId(testGuest.getId());

        given()
                .contentType(ContentType.JSON)
                .body(updateRequest)
                .when()
                .patch("/bookings/{id}", savedBooking.getId())
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", is(savedBooking.getId().intValue()))
                .body("checkInDate", is(updateRequest.getCheckInDate().format(DATE_TIME_FORMATTER)))
                .body("checkOutDate", is(updateRequest.getCheckOutDate().format(DATE_TIME_FORMATTER)));
    }

    @Test
    void testCancelBooking() {
        var bookingEntity = new BookingEntity();
        bookingEntity.setCheckInDate(LocalDateTime.now().plusDays(1));
        bookingEntity.setCheckOutDate(LocalDateTime.now().plusDays(2));
        bookingEntity.setProperty(testProperty);
        bookingEntity.setGuest(testGuest);
        var savedBooking = bookingRepository.save(bookingEntity);

        given()
                .contentType(ContentType.JSON)
                .when()
                .post("/bookings/{id}/cancel", savedBooking.getId())
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", is(savedBooking.getId().intValue()))
                .body("status", is("CANCELLED"));
    }

    @Test
    void testRebookBooking() {
        var bookingEntity = new BookingEntity();
        bookingEntity.setCheckInDate(LocalDateTime.now().plusDays(1));
        bookingEntity.setCheckOutDate(LocalDateTime.now().plusDays(2));
        bookingEntity.setProperty(testProperty);
        bookingEntity.setGuest(testGuest);
        bookingEntity.setStatus(BookingStatusEnum.CANCELLED);
        var savedBooking = bookingRepository.save(bookingEntity);

        given()
                .contentType(ContentType.JSON)
                .when()
                .post("/bookings/{id}/rebook", savedBooking.getId())
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", is(savedBooking.getId().intValue()))
                .body("status", is("ACTIVE"));
    }

    @Test
    void testDeleteBooking() {
        var bookingEntity = new BookingEntity();
        bookingEntity.setCheckInDate(LocalDateTime.now().plusDays(1));
        bookingEntity.setCheckOutDate(LocalDateTime.now().plusDays(2));
        bookingEntity.setProperty(testProperty);
        bookingEntity.setGuest(testGuest);
        var savedBooking = bookingRepository.save(bookingEntity);

        given()
                .contentType(ContentType.JSON)
                .when()
                .delete("/bookings/{id}", savedBooking.getId())
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        assertFalse(bookingRepository.findById(savedBooking.getId()).isPresent());
    }
}
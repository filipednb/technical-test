package filipednb.github.com.hostfullyapi.integration;

import filipednb.github.com.hostfullyapi.HostfullyapiApplication;
import filipednb.github.com.hostfullyapi.domain.block.BlockEntity;
import filipednb.github.com.hostfullyapi.domain.block.BlockRepository;
import filipednb.github.com.hostfullyapi.domain.block.BlockRequest;
import filipednb.github.com.hostfullyapi.domain.booking.BookingRepository;
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

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest(classes = HostfullyapiApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class BlockResourceIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private BlockRepository blockRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    private PropertyEntity testProperty;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        blockRepository.deleteAll();
        propertyRepository.deleteAll();
        bookingRepository.deleteAll();
        userRepository.deleteAll();

        // Create and save a test user
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
    }

    @AfterEach
    void cleanUp() {
        blockRepository.deleteAll(); // Ensure blocks are deleted before properties
        bookingRepository.deleteAll();
        propertyRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testGetAllBlocks() {
        var blockEntity = new BlockEntity();
        blockEntity.setStartDate(LocalDateTime.now().plusDays(1));
        blockEntity.setEndDate(LocalDateTime.now().plusDays(2));
        blockEntity.setProperty(testProperty);
        blockRepository.save(blockEntity);

        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/blocks")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("size()", is(1))
                .body("[0].id", notNullValue());
    }

    @Test
    void testGetBlockById() {
        var blockEntity = new BlockEntity();
        blockEntity.setStartDate(LocalDateTime.now().plusDays(1));
        blockEntity.setEndDate(LocalDateTime.now().plusDays(2));
        blockEntity.setProperty(testProperty);
        var savedBlock = blockRepository.save(blockEntity);

        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/blocks/{id}", savedBlock.getId())
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", is(savedBlock.getId().intValue()))
                .body("startDate", notNullValue())
                .body("endDate", notNullValue());
    }

    @Test
    void testCreateBlock() {
        var blockRequest = new BlockRequest();
        blockRequest.setStartDate(LocalDateTime.now().plusDays(1));
        blockRequest.setEndDate(LocalDateTime.now().plusDays(2));
        blockRequest.setPropertyId(testProperty.getId());

        given()
                .contentType(ContentType.JSON)
                .body(blockRequest)
                .when()
                .post("/blocks")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .header("Location", containsString("/blocks/"))
                .body("id", notNullValue())
                .body("startDate", notNullValue())
                .body("endDate", notNullValue());
    }

    @Test
    void testUpdateBlock() {
        var blockEntity = new BlockEntity();
        blockEntity.setStartDate(LocalDateTime.now().plusDays(1));
        blockEntity.setEndDate(LocalDateTime.now().plusDays(2));
        blockEntity.setProperty(testProperty);
        var savedBlock = blockRepository.save(blockEntity);

        var updateRequest = new BlockRequest();
        updateRequest.setStartDate(LocalDateTime.now().plusDays(3));
        updateRequest.setEndDate(LocalDateTime.now().plusDays(4));
        updateRequest.setPropertyId(testProperty.getId());

        given()
                .contentType(ContentType.JSON)
                .body(updateRequest)
                .when()
                .patch("/blocks/{id}", savedBlock.getId())
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", is(savedBlock.getId().intValue()))
                .body("startDate", is(updateRequest.getStartDate().toString()))
                .body("endDate", is(updateRequest.getEndDate().toString()));
    }

    @Test
    void testDeleteBlock() {
        var blockEntity = new BlockEntity();
        blockEntity.setStartDate(LocalDateTime.now().plusDays(1));
        blockEntity.setEndDate(LocalDateTime.now().plusDays(2));
        blockEntity.setProperty(testProperty);
        var savedBlock = blockRepository.save(blockEntity);

        given()
                .contentType(ContentType.JSON)
                .when()
                .delete("/blocks/{id}", savedBlock.getId())
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        assertFalse(blockRepository.findById(savedBlock.getId()).isPresent());
    }
}
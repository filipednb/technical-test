package filipednb.github.com.hostfullyapi.integration;

import filipednb.github.com.hostfullyapi.HostfullyapiApplication;
import filipednb.github.com.hostfullyapi.domain.property.PropertyEntity;
import filipednb.github.com.hostfullyapi.domain.property.PropertyRepository;
import filipednb.github.com.hostfullyapi.domain.property.PropertyRequest;
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

import java.net.URI;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest(classes = HostfullyapiApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class PropertyResourceIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private UserRepository userRepository;

    private UserEntity testOwner;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        propertyRepository.deleteAll();
        userRepository.deleteAll();

        // Create and save a test owner
        testOwner = new UserEntity();
        testOwner.setType(UserTypeEnum.OWNER);
        testOwner.setName("Jack Spencer");
        testOwner.setEmail("jack.spec@dummy.com");
        testOwner = userRepository.save(testOwner);
    }

    @AfterEach
    void cleanUp() {
        propertyRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testGetAllProperties() {
        var propertyEntity = new PropertyEntity();
        propertyEntity.setName("Ocean View Apartment");
        propertyEntity.setLocation("Miami, FL");
        propertyEntity.setOwner(testOwner);
        propertyRepository.save(propertyEntity);

        given()
            .contentType(ContentType.JSON)
        .when()
            .get("/properties")
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("size()", is(1))
            .body("[0].id", notNullValue());
    }

    @Test
    void testGetPropertyById() {
        var propertyEntity = new PropertyEntity();
        propertyEntity.setName("Ocean View Apartment");
        propertyEntity.setLocation("Miami, FL");
        propertyEntity.setOwner(testOwner);
        var savedProperty = propertyRepository.save(propertyEntity);

        given()
            .contentType(ContentType.JSON)
        .when()
            .get("/properties/{id}", savedProperty.getId())
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("id", is(savedProperty.getId().intValue()))
            .body("name", notNullValue())
            .body("location", notNullValue());
    }

    @Test
    void testCreateProperty() {
        var propertyRequest = new PropertyRequest();
        propertyRequest.setName("Ocean View Apartment");
        propertyRequest.setLocation("Miami, FL");
        propertyRequest.setOwnerId(testOwner.getId());

        given()
            .contentType(ContentType.JSON)
            .body(propertyRequest)
        .when()
            .post("/properties")
        .then()
            .statusCode(HttpStatus.CREATED.value())
            .header("Location", containsString("/properties/"))
            .body("id", notNullValue())
            .body("name", is("Ocean View Apartment"))
            .body("location", is("Miami, FL"))
            .body("owner.id", is(testOwner.getId().intValue()));
    }

    @Test
    void testUpdateProperty() {
        var propertyEntity = new PropertyEntity();
        propertyEntity.setName("Ocean View Apartment");
        propertyEntity.setLocation("Miami, FL");
        propertyEntity.setOwner(testOwner);
        var savedProperty = propertyRepository.save(propertyEntity);

        var propertyRequest = new PropertyRequest();
        propertyRequest.setName("Updated Ocean View Apartment");
        propertyRequest.setLocation("Updated Miami, FL");
        propertyRequest.setOwnerId(testOwner.getId());

        given()
            .contentType(ContentType.JSON)
            .body(propertyRequest)
        .when()
            .patch("/properties/{id}", savedProperty.getId())
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("id", is(savedProperty.getId().intValue()))
            .body("name", is("Updated Ocean View Apartment"))
            .body("location", is("Updated Miami, FL"))
            .body("owner.id", is(testOwner.getId().intValue()));
    }
}
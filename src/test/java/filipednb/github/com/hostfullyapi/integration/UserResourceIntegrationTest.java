package filipednb.github.com.hostfullyapi.integration;

import filipednb.github.com.hostfullyapi.HostfullyapiApplication;
import filipednb.github.com.hostfullyapi.domain.user.UserEntity;
import filipednb.github.com.hostfullyapi.domain.user.UserRepository;
import filipednb.github.com.hostfullyapi.domain.user.UserRequest;
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

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest(classes = HostfullyapiApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class UserResourceIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        userRepository.deleteAll();
    }

    @AfterEach
    void cleanUp() {
        userRepository.deleteAll();
    }

    @Test
    void testGetAllUsers() {
        var userEntity = new UserEntity();
        userEntity.setType(UserTypeEnum.OWNER);
        userEntity.setName("Jack Spencer");
        userEntity.setEmail("jack.spec@dummy.com");
        userRepository.save(userEntity);

        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/users")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("size()", is(1))
                .body("[0].id", notNullValue());
    }

    @Test
    void testGetUserById() {
        var userEntity = new UserEntity();
        userEntity.setType(UserTypeEnum.OWNER);
        userEntity.setName("Jack Spencer");
        userEntity.setEmail("jack.spec@dummy.com");
        var savedUser = userRepository.save(userEntity);

        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/users/{id}", savedUser.getId())
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", is(savedUser.getId().intValue()))
                .body("name", is("Jack Spencer"))
                .body("email", is("jack.spec@dummy.com"));
    }

    @Test
    void testCreateUser() {
        var userRequest = new UserRequest();
        userRequest.setType(UserTypeEnum.OWNER);
        userRequest.setName("Jack Spencer");
        userRequest.setEmail("jack.spec@dummy.com");

        given()
                .contentType(ContentType.JSON)
                .body(userRequest)
                .when()
                .post("/users")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .header("Location", containsString("/users/"))
                .body("id", notNullValue())
                .body("name", is("Jack Spencer"))
                .body("email", is("jack.spec@dummy.com"));
    }

    @Test
    void testUpdateUser() {
        var userEntity = new UserEntity();
        userEntity.setType(UserTypeEnum.OWNER);
        userEntity.setName("Jack Spencer");
        userEntity.setEmail("jack.spec@dummy.com");
        var savedUser = userRepository.save(userEntity);

        var userRequest = new UserRequest();
        userRequest.setType(UserTypeEnum.OWNER);
        userRequest.setName("Updated Jack Spencer");
        userRequest.setEmail("updated.jack.spec@dummy.com");

        given()
                .contentType(ContentType.JSON)
                .body(userRequest)
                .when()
                .patch("/users/{id}", savedUser.getId())
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", is(savedUser.getId().intValue()))
                .body("name", is("Updated Jack Spencer"))
                .body("email", is("updated.jack.spec@dummy.com"));
    }
}
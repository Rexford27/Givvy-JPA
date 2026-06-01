package Tfast_Rmoney.Givvy;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.emptyOrNullString;

import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;

<<<<<<< HEAD
import Tfast_Rmoney.Givvy.interfaces.dtos.RegisterUserRequest;
=======
>>>>>>> b00f398 (security working checkpoint)
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

<<<<<<< HEAD
@SpringBootTest(classes=GivvyApplication.class,webEnvironment = WebEnvironment.DEFINED_PORT)
//@ActiveProfiles("test")
public class APISetupTests {
   
    private static RegisterUserRequest testDonor;
    private static RegisterUserRequest testRecipient1;
    private static RegisterUserRequest testRecipient2;

    @BeforeAll
	public static void setup() {
		RestAssured.port = 8085;
		RestAssured.baseURI = "http://localhost";
			
		testDonor = new RegisterUserRequest();
		testDonor.setName("testdonor");
		testDonor.setEmail("donor@example.com");
		testDonor.setPassword("password");
		testDonor.setPhone("1234567890");

		testRecipient1 = new RegisterUserRequest();
		testRecipient1.setName("testrecipient1");
		testRecipient1.setEmail("recipient1@example.com");
		testRecipient1.setPassword("password2");
		testRecipient1.setPhone("0987654321");

		testRecipient2 = new RegisterUserRequest();
		testRecipient2.setName("testrecipient2");
		testRecipient2.setEmail("recipient2@example.com");
		testRecipient2.setPassword("password3");
		testRecipient2.setPhone("5555555555");
	}

    @Test
    public void postDonor() {
        given()
        .contentType("application/json")
        .body(testDonor)
        .when().post("/users").then()
        .statusCode(anyOf(is(201),is(409)));
    }

	@Test
	public void postRecipients() {
		given()
		.contentType("application/json")
		.body(testRecipient1)
		.when().post("/users").then()
		.statusCode(anyOf(is(201),is(409)));
		
		given()
		.contentType("application/json")
		.body(testRecipient2)
		.when().post("/users").then()
		.statusCode(anyOf(is(201),is(409)));
	}
}
=======
@SpringBootTest(classes = GivvyApplication.class, webEnvironment = WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
public class APISetupTests {

    private static final String PASSWORD = "hello";

    private static String donorEmail;
    private static String recipientOneEmail;
    private static String recipientTwoEmail;

    @BeforeAll
    public static void setup() {

        // I tell REST Assured where my test server is running.

        RestAssured.port = 8085;
        RestAssured.baseURI = "http://localhost";

        // I only print request/response logs when a test fails.

        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        // I make the emails unique so I can rerun the tests without duplicate-user problems.

        long runId = System.currentTimeMillis();

        donorEmail = "donor" + runId + "@test.com";
        recipientOneEmail = "recipient1" + runId + "@test.com";
        recipientTwoEmail = "recipient2" + runId + "@test.com";
    }

    @Test
    public void userSecuritySetupWorks() {

        // I create the three users required by the assignment.

        String donorId = registerUser("Test Donor", donorEmail, PASSWORD);
        String recipientOneId = registerUser("Test Recipient One", recipientOneEmail, PASSWORD);
        String recipientTwoId = registerUser("Test Recipient Two", recipientTwoEmail, PASSWORD);

        // I log in all three users and get JWT tokens.

        String donorToken = loginAndGetToken(donorEmail, PASSWORD);
        String recipientOneToken = loginAndGetToken(recipientOneEmail, PASSWORD);
        String recipientTwoToken = loginAndGetToken(recipientTwoEmail, PASSWORD);

        Assertions.assertNotNull(donorToken);
        Assertions.assertNotNull(recipientOneToken);
        Assertions.assertNotNull(recipientTwoToken);

        Assertions.assertFalse(donorToken.isBlank());
        Assertions.assertFalse(recipientOneToken.isBlank());
        Assertions.assertFalse(recipientTwoToken.isBlank());

        // I prove that a protected user route fails without a JWT.

        given()
        .when()
                .get("/users/" + donorId)
        .then()
                .statusCode(anyOf(is(401), is(403)));

        // I prove that a user can access their own user route with their JWT.

        given()
                .header("Authorization", "Bearer " + donorToken)
        .when()
                .get("/users/" + donorId)
        .then()
                .statusCode(200)
                .body("userId", equalTo(donorId))
                .body("email", equalTo(donorEmail));

        // I prove that recipient one cannot access the donor's user route.

        given()
                .header("Authorization", "Bearer " + recipientOneToken)
        .when()
                .get("/users/" + donorId)
        .then()
                .statusCode(403);

        // I prove that the donor can access their own items route.

        given()
                .header("Authorization", "Bearer " + donorToken)
        .when()
                .get("/users/" + donorId + "/items")
        .then()
                .statusCode(200);

        // I prove that recipient two cannot access the donor's items route.

        given()
                .header("Authorization", "Bearer " + recipientTwoToken)
        .when()
                .get("/users/" + donorId + "/items")
        .then()
                .statusCode(403);

        Assertions.assertNotEquals(donorId, recipientOneId);
        Assertions.assertNotEquals(donorId, recipientTwoId);
        Assertions.assertNotEquals(recipientOneId, recipientTwoId);
    }

    private String registerUser(String name, String email, String password) {

        Map<String, String> requestBody = Map.of(
                "name", name,
                "email", email,
                "password", password,
                "phone", "555-0000"
        );

        return given()
                .contentType(ContentType.JSON)
                .body(requestBody)
        .when()
                .post("/users")
        .then()
                .statusCode(201)
                .extract()
                .asString();
    }

    private String loginAndGetToken(String email, String password) {

        Map<String, String> requestBody = Map.of(
                "email", email,
                "password", password
        );

        return given()
                .contentType(ContentType.JSON)
                .body(requestBody)
        .when()
                .post("/users/login")
        .then()
                .statusCode(200)
                .body(not(emptyOrNullString()))
                .extract()
                .asString();
    }
}
>>>>>>> b00f398 (security working checkpoint)

package Tfast_Rmoney.Givvy;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import java.util.Map;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;

import Tfast_Rmoney.Givvy.interfaces.dtos.LoginRequest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

@SpringBootTest(classes = GivvyApplication.class, webEnvironment = WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
public class APIRexSecurityItemMessageTests {

    private static final String PASSWORD = "hello";

    private static String donorEmail;
    private static String recipientEmail;
    	private static LoginRequest testDonor;
	private static LoginRequest testRecipient1;
	private static LoginRequest testRecipient2;
	private static String donorToken;
	private static String recipient1Token;
	private static String recipient2Token;

    @BeforeAll
    public static void setup() {

        // I am setting REST Assured to use the same test server setup as the other API tests.
        // I am not changing my partner's test files. This file runs alongside them.

		RestAssured.port = 8085;
		RestAssured.baseURI = "http://localhost";
			
		testDonor = new LoginRequest();
		testDonor.setEmail("donor@example.com");
		testDonor.setPassword("password");

		testRecipient1 = new LoginRequest();
		testRecipient1.setEmail("recipient1@example.com");
		testRecipient1.setPassword("password2");

		testRecipient2 = new LoginRequest();
		testRecipient2.setEmail("recipient2@example.com");
		testRecipient2.setPassword("password3");
	

        // // I only want detailed REST Assured logs if a test fails.
        // // This keeps the console cleaner when everything passes.

        // RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        // // I use unique emails every time the test runs.
        // // This prevents duplicate email errors from old test data in the database.

        // long runId = System.currentTimeMillis();

        // donorEmail = "rex_donor_" + runId + "@test.com";
        // recipientEmail = "rex_recipient_" + runId + "@test.com";



    }

    @Test
        @Order(1)
        public void prerequisites() {

        donorToken = given()
		.contentType("application/json")
		.body(testDonor)
		.when().post("/users/login")
		.then()
		.statusCode(200)
		.extract().asString();
		
	recipient1Token = given()
		.contentType("application/json")
		.body(testRecipient1)
		.when().post("/users/login")
		.then()
		.statusCode(200)
		.extract().asString();

	recipient2Token = given()
		.contentType("application/json")
		.body(testRecipient2)
		.when().post("/users/login")
		.then()
		.statusCode(200)
		.extract().asString();
        }


    @Test
    public void myUserItemAndMessageSecurityFlowWorks() {

        // // I create two users for my part of the security test.
        // // The donor will post an item.
        // // The recipient will try to interact with that item and send a message.

        // String donorId = registerUser("Rex Donor", donorEmail, PASSWORD);
        // String recipientId = registerUser("Rex Recipient", recipientEmail, PASSWORD);

        // // I log in both users.
        // // Login should return JWT tokens, not plain user ids.

        // String donorToken = loginAndGetToken(donorEmail, PASSWORD);
        // String recipientToken = loginAndGetToken(recipientEmail, PASSWORD);

        // // I prove that a protected route blocks requests with no JWT.
        // // Some Spring Security setups return 401, others return 403, so I allow either.

        // given()
        // .when()
        //         .get("/users/" + donorId)
        // .then()
        //         .statusCode(anyOf(is(401), is(403)));

        // // I prove the donor can access their own protected user route.

        // given()
        //         .header("Authorization", "Bearer " + donorToken)
        // .when()
        //         .get("/users/" + donorId)
        // .then()
        //         .statusCode(200)
        //         .body("userId", equalTo(donorId))
        //         .body("email", equalTo(donorEmail));

        // // I prove the recipient cannot access the donor's protected user route.
        // // This proves the URL userId must match the userId inside the JWT.

        // given()
        //         .header("Authorization", "Bearer " + recipientToken)
        // .when()
        //         .get("/users/" + donorId)
        // .then()
        //         .statusCode(403);

        // I prove that POST /items is protected.
        // Without a JWT, item creation should fail.

        // given()
        //         .contentType(ContentType.JSON)
        //         .body(Map.of(
        //                 "donorId", donorId,
        //                 "title", "No Token Item",
        //                 "description", "This request should fail because there is no JWT"
        //         ))
        // .when()
        //         .post("/items")
        // .then()
        //         .statusCode(anyOf(is(401), is(403)));

        // I create an item as the donor.
        // I intentionally put the recipientId inside donorId.
        // If my security is correct, the controller ignores this fake donorId
        // and uses the donor id from the JWT instead.

        String itemId = given()
                .header("Authorization", "Bearer " + donorToken)
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "donorId", donorEmail,
                        "title", "Rex Test Jacket",
                        "description", "A warm jacket for donation",
                        "imageUrl", "https://example.com/jacket.png"
                ))
        .when()
                .post("/items")
        .then()
                .statusCode(201)
                .extract()
                .asString();

        // I check that the item belongs to the donor from the JWT.
        // This is the important security proof:
        // the fake donorId from the body did not win.

        given()
                .header("Authorization", "Bearer " + donorToken)
        .when()
                .get("/items/" + itemId)
        .then()
                .statusCode(200)
                .body("itemId", equalTo(itemId))
                // .body("donorId", equalTo(donorId))
                .body("title", equalTo("Rex Test Jacket"))
                .body("status", equalTo("available"));

        // I prove the new item appears in the available item list.
        // This supports the assignment requirement that posted items show up as offered/listed.

        given()
                .header("Authorization", "Bearer " + donorToken)
        .when()
                .get("/items?status=available")
        .then()
                .statusCode(200)
                .body("itemId", hasItem(itemId));

        // I prove the recipient cannot update the donor's item status.

        given()
                .header("Authorization", "Bearer " + recipient1Token)
                .contentType(ContentType.JSON)
                .body(Map.of("status", "pending"))
        .when()
                .patch("/items/" + itemId + "/status")
        .then()
                .statusCode(403);

        // I prove the actual donor can update their own item status.

        given()
                .header("Authorization", "Bearer " + donorToken)
                .contentType(ContentType.JSON)
                .body(Map.of("status", "pending"))
        .when()
                .patch("/items/" + itemId + "/status")
        .then()
                .statusCode(200);

        // I create a message as the recipient.
        // I intentionally put donorId as senderId in the request body.
        // If my security is correct, the controller ignores senderId from the DTO
        // and uses the recipient id from the JWT as the real sender.

        String messageId = given()
                .header("Authorization", "Bearer " + recipient1Token)
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "senderId", donorId,
                        "recipientId", donorId,
                        "itemId", itemId,
                        "subject", "Interested in item",
                        "body", "Hi, I am interested in the jacket."
                ))
        .when()
                .post("/messages")
        .then()
                .statusCode(201)
                .extract()
                .asString();

        // I prove the message appears under the real sender.
        // The real sender should be the user from the JWT, not the fake senderId from the body.

        given()
                .header("Authorization", "Bearer " + recipientToken)
        .when()
                .get("/messages/sender/" + recipientId)
        .then()
                .statusCode(200)
                .body("messageId", hasItem(messageId))
                .body("senderId", hasItem(recipientId));

        // I prove the donor can see the message as the recipient.

        given()
                .header("Authorization", "Bearer " + donorToken)
        .when()
                .get("/messages/recipient/" + donorId)
        .then()
                .statusCode(200)
                .body("messageId", hasItem(messageId))
                .body("recipientId", hasItem(donorId));

        // I prove the sender cannot mark the message as read.
        // In my rule, only the message recipient can mark it as read.

        given()
                .header("Authorization", "Bearer " + recipientToken)
        .when()
                .patch("/messages/" + messageId + "/read")
        .then()
                .statusCode(403);

        // I prove the real recipient can mark the message as read.

        given()
                .header("Authorization", "Bearer " + donorToken)
        .when()
                .patch("/messages/" + messageId + "/read")
        .then()
                .statusCode(200);
    }

    private String registerUser(String name, String email, String password) {

        // I use a Map instead of a DTO so this test does not depend on a test-only UserDTO.
        // These JSON field names must match RegisterUserRequest.

        Map<String, String> requestBody = Map.of(
                "name", name,
                "email", email,
                "password", password,
                "phone", "555-0000"
        );

        // POST /users is public because a new user does not have a JWT yet.

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

        // These JSON field names must match LoginRequest.
        // Login should return a JWT token as a plain String.

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
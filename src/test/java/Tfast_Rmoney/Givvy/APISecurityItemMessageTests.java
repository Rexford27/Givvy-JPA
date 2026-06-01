package Tfast_Rmoney.Givvy;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.emptyOrNullString;

import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

@SpringBootTest(classes = GivvyApplication.class, webEnvironment = WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
public class APISecurityItemMessageTests {

    private static final String PASSWORD = "hello";

    private static String donorEmail;
    private static String recipientEmail;

    @BeforeAll
    public static void setup() {

        RestAssured.port = 8085;
        RestAssured.baseURI = "http://localhost";
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        long runId = System.currentTimeMillis();

        donorEmail = "itemdonor" + runId + "@test.com";
        recipientEmail = "itemrecipient" + runId + "@test.com";
    }

    @Test
    public void itemAndMessageSecurityFlowWorks() {

        String donorId = registerUser("Item Donor", donorEmail, PASSWORD);
        String recipientId = registerUser("Item Recipient", recipientEmail, PASSWORD);

        String donorToken = loginAndGetToken(donorEmail, PASSWORD);
        String recipientToken = loginAndGetToken(recipientEmail, PASSWORD);

        // I prove that creating an item without a JWT is blocked.

        given()
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "donorId", donorId,
                        "title", "No Token Item",
                        "description", "This should fail"
                ))
        .when()
                .post("/items")
        .then()
                .statusCode(anyOf(is(401), is(403)));

        // I create an item as the donor.
        // I intentionally put the recipientId inside donorId to prove the controller ignores fake donorId.
        // The real donor should come from the donorToken.

        String itemId = given()
                .header("Authorization", "Bearer " + donorToken)
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "donorId", recipientId,
                        "title", "Test Jacket",
                        "description", "A warm jacket for donation",
                        "imageUrl", "https://example.com/jacket.png"
                ))
        .when()
                .post("/items")
        .then()
                .statusCode(201)
                .extract()
                .asString();

        // I check that the saved item belongs to the donor from the JWT,
        // not the fake donorId from the request body.

        given()
                .header("Authorization", "Bearer " + donorToken)
        .when()
                .get("/items/" + itemId)
        .then()
                .statusCode(200)
                .body("itemId", equalTo(itemId))
                .body("donorId", equalTo(donorId))
                .body("title", equalTo("Test Jacket"))
                .body("status", equalTo("available"));

        // I prove the item appears in the available item list.

        given()
                .header("Authorization", "Bearer " + donorToken)
        .when()
                .get("/items?status=available")
        .then()
                .statusCode(200)
                .body("itemId", hasItem(itemId));

        // I prove another user cannot update the donor's item status.

        given()
                .header("Authorization", "Bearer " + recipientToken)
                .contentType(ContentType.JSON)
                .body(Map.of("status", "pending"))
        .when()
                .patch("/items/" + itemId + "/status")
        .then()
                .statusCode(403);

        // I prove the donor can update their own item status.

        given()
                .header("Authorization", "Bearer " + donorToken)
                .contentType(ContentType.JSON)
                .body(Map.of("status", "pending"))
        .when()
                .patch("/items/" + itemId + "/status")
        .then()
                .statusCode(200);

        // I create a message as the recipient to the donor.
        // I intentionally put donorId in senderId to prove senderId from the body is ignored.
        // The real sender should come from recipientToken.

        String messageId = given()
                .header("Authorization", "Bearer " + recipientToken)
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

        // I prove the message is listed under the real sender, which is the recipient user.

        given()
                .header("Authorization", "Bearer " + recipientToken)
        .when()
                .get("/messages/sender/" + recipientId)
        .then()
                .statusCode(200)
                .body("messageId", hasItem(messageId))
                .body("senderId", hasItem(recipientId));

        // I prove the donor can see the message as the recipient of the message.

        given()
                .header("Authorization", "Bearer " + donorToken)
        .when()
                .get("/messages/recipient/" + donorId)
        .then()
                .statusCode(200)
                .body("messageId", hasItem(messageId))
                .body("recipientId", hasItem(donorId));

        // I prove the sender cannot mark the message as read.
        // Only the recipient should be able to mark it as read.

        given()
                .header("Authorization", "Bearer " + recipientToken)
        .when()
                .patch("/messages/" + messageId + "/read")
        .then()
                .statusCode(403);

        // I prove the actual message recipient can mark the message as read.

        given()
                .header("Authorization", "Bearer " + donorToken)
        .when()
                .patch("/messages/" + messageId + "/read")
        .then()
                .statusCode(200);
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
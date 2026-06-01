package Tfast_Rmoney.Givvy;

import static io.restassured.RestAssured.given;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;

import Tfast_Rmoney.Givvy.entities.User;
import Tfast_Rmoney.Givvy.interfaces.dtos.LoginRequest;
import Tfast_Rmoney.Givvy.interfaces.dtos.RegisterUserRequest;
import io.restassured.RestAssured;
import static org.hamcrest.Matchers.*;

import org.apache.tomcat.util.http.parser.Authorization;

@SpringBootTest(classes=GivvyApplication.class,webEnvironment = WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class APIInterestandAppointmentTests {
	private static LoginRequest testDonor;
	private static LoginRequest testRecipient1;
	private static LoginRequest testRecipient2;
	private static String donorToken;
	private static String recipient1Token;
	private static String recipient2Token;

	@BeforeAll
	public static void setup() {
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

		given()
		.header("Authorization","Bearer"+donorToken)
		.when().get("items")
		
	}


	@Test
	@Order(2)
	public void testPostInterest() {
	}


}

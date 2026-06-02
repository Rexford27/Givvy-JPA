package Tfast_Rmoney.Givvy;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import Tfast_Rmoney.Givvy.interfaces.dtos.RegisterUserRequest;



import io.restassured.RestAssured;

@SpringBootTest(classes=GivvyApplication.class,webEnvironment = WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
public class APISetupTests {

private static RegisterUserRequest testDonor;
private static RegisterUserRequest testRecipient1;
private static RegisterUserRequest testRecipient2;

    @BeforeAll
	public static void setup() {
		RestAssured.port = 8085;
		RestAssured.baseURI = "http://localhost";
			
		testDonor = new RegisterUserRequest();
		testDonor.setName("TestDonor");
                testDonor.setEmail("donor@example.com");
		testDonor.setPassword("password");
			
		testRecipient1 = new RegisterUserRequest();
		testRecipient1.setName("Recipient1");
                testRecipient1.setEmail("recipient1@example.com");
		testRecipient1.setPassword("password2");

		testRecipient2 = new RegisterUserRequest();
		testRecipient2.setName("Recipient2");
		testRecipient2.setEmail("recipient2@example.com");
		testRecipient2.setPassword("password3");
	}
	
	@Test
	public void postSeller() {
		given()
		.contentType("application/json")
		.body(testDonor)
		.when().post("/users").then()
		.statusCode(anyOf(is(201),is(409)));
	}
	
	@Test
	public void postBuyers() {
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
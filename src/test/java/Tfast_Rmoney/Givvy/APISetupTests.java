package Tfast_Rmoney.Givvy;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;

import Tfast_Rmoney.Givvy.interfaces.dtos.RegisterUserRequest;
import io.restassured.RestAssured;

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

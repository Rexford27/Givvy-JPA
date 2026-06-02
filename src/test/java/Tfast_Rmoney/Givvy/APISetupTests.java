package Tfast_Rmoney.Givvy;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;


import io.restassured.RestAssured;

@SpringBootTest(classes=GivvyApplication.class,webEnvironment = WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
public class APISetupTests {
    @BeforeAll
	public static void setup() {
		RestAssured.port = 8085;
		RestAssured.baseURI = "http://localhost";
			
		testSeller = new UserDTO();
		testSeller.setName("TestSeller");
		testSeller.setPassword("hello");
			
		testBuyerOne = new UserDTO();
		testBuyerOne.setName("BuyerOne");
		testBuyerOne.setPassword("hello");
		testBuyerTwo = new UserDTO();
		testBuyerTwo.setName("BuyerTwo");
		testBuyerTwo.setPassword("hello");
	}
	
	@Test
	public void postSeller() {
		given()
		.contentType("application/json")
		.body(testSeller)
		.when().post("/users").then()
		.statusCode(anyOf(is(201),is(409)));
	}
	
	@Test
	public void postBuyers() {
		given()
		.contentType("application/json")
		.body(testBuyerOne)
		.when().post("/users").then()
		.statusCode(anyOf(is(201),is(409)));
		
		given()
		.contentType("application/json")
		.body(testBuyerTwo)
		.when().post("/users").then()
		.statusCode(anyOf(is(201),is(409)));
	}
}
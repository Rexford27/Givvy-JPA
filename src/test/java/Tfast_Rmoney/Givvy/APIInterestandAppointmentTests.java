package Tfast_Rmoney.Givvy;

import static io.restassured.RestAssured.given;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import Tfast_Rmoney.Givvy.entities.User;
import Tfast_Rmoney.Givvy.interfaces.dtos.AppointmentDTO;
import Tfast_Rmoney.Givvy.interfaces.dtos.AppointmentSchedulingDTO;
import Tfast_Rmoney.Givvy.interfaces.dtos.InterestDTO;
import Tfast_Rmoney.Givvy.interfaces.dtos.LoginRequest;
import Tfast_Rmoney.Givvy.interfaces.dtos.OfferDTO;
import Tfast_Rmoney.Givvy.interfaces.dtos.OfferResponse;
import Tfast_Rmoney.Givvy.interfaces.dtos.RegisterUserRequest;
import Tfast_Rmoney.Givvy.security.JwtService;
import io.restassured.RestAssured;
import static org.hamcrest.Matchers.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.tomcat.util.http.parser.Authorization;

@SpringBootTest(classes=GivvyApplication.class,webEnvironment = WebEnvironment.DEFINED_PORT)
//@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class APIInterestandAppointmentTests {
	private static LoginRequest testDonor;
	private static LoginRequest testRecipient1;
	private static LoginRequest testRecipient2;
	private static String donorToken;
	private static String recipient1Token;
	private static String recipient2Token;
	private static String itemId;
	private static Integer interestId;
	@Autowired
	private JwtService jwtService;
 

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

		itemId = given()
		.header("Authorization","Bearer "+donorToken)
		.when().get("/items/donor")
		.then().statusCode(200)
		.extract().path("[0].itemId");	

	}


	@Test
	@Order(2)
	public void testPostInterest() {

		//Post interests for both users
		InterestDTO Interest1 = new InterestDTO();
		Interest1.setItemId(itemId);

		InterestDTO Interest2 = new InterestDTO();
		Interest2.setItemId(itemId);

		given()
		.header("Authorization","Bearer "+recipient1Token)
		.contentType("application/json")
		.body(Interest1)
		.when().post("/interests")
		.then().statusCode(200);
		
		given()
		.header("Authorization","Bearer "+recipient2Token)
		.contentType("application/json")
		.body(Interest2)
		.when().post("/interests")
		.then().statusCode(200);

		//Test to see if the interests show up for both users
		given()
		.header("Authorization","Bearer "+recipient1Token)
		.when().get("/interests")
		.then().statusCode(200)
		.body("$.size()",greaterThan(0));

		given()
		.header("Authorization","Bearer "+recipient2Token)
		.when().get("/interests")
		.then().statusCode(200)
		.body("$.size()",greaterThan(0));
		
 	}

	@Test
	@Order(3)
	public void testOffer() {

		//Set up offer to recipient 1
		interestId = given()
		.header("Authorization","Bearer "+recipient1Token)
		.when().get("/interests")
		.then().statusCode(200)
		.extract().path("[0].id");

		OfferDTO offer = new OfferDTO();
		offer.setInterestId(interestId);
		offer.setDonorId(jwtService.getSubject(donorToken));
		offer.setRecipientId(jwtService.getSubject(recipient1Token));
		offer.setStatus(0);

		//Make offer to recipient 1
		given()
		.header("Authorization","Bearer "+donorToken)
		.contentType("application/json")
		.body(offer)
		.when().post("/interests/offers")
		.then().statusCode(200);

		//Make sure the offer appears
		Integer offerId = given()
		.header("Authorization","Bearer "+recipient1Token)
		.when().get("/interests/offers/recipient")
		.then().statusCode(200)
		.body("$.size()",greaterThan(0))
		.extract().path("[0].offerId");

		//Recipient 1 accepts offer
		OfferResponse offerResponse = new OfferResponse();
		offerResponse.setOfferId(offerId); 
		offerResponse.setAccepted(true);

		given()
		.header("Authorization","Bearer "+recipient1Token)
		.contentType("application/json")
		.body(offerResponse)
		.when().post("/interests/offerresponse")
		.then().statusCode(200);
	}

	@Test
	@Order(4)
	public void testScheduleAppointments() {
		//get a random location
		Integer locationId = given()
		.header("Authorization","Bearer "+donorToken)
		.when().get("/transfer-sites")
		.then().statusCode(200)
		.extract().path("[0].id");

		//Generate 4 appt times
		List<AppointmentSchedulingDTO> schedules = new ArrayList<>();
		for(int i = 0; i < 4; i++) {
			AppointmentSchedulingDTO schedule = new AppointmentSchedulingDTO();
			schedule.setLocationId(locationId);
			schedule.setInterestId(interestId);
			schedule.setStartTime("2026-06-01T1"+i+":30:00");
			schedules.add(schedule);
		}

		//Donor proposes four timeslots for the appointment
		given()
		.header("Authorization","Bearer "+donorToken)
		.contentType("application/json")
		.body(schedules)
		.when().post("/appointmentschedules")
		.then().statusCode(200);

		//Recipient chooses the second timeslot
		String startTime = given()
		.header("Authorization","Bearer "+recipient1Token)
		.when().get("/appointmentschedules")
		.then().statusCode(200)
		.extract().path("[0].startTime");


		String[] parts = startTime.split("T");
		String datePart = parts[0];
		String timePart = parts[1]; 


		AppointmentDTO appt = new AppointmentDTO();
		appt.setInterestId(interestId);
		appt.setLocationId(locationId);
		appt.setDay(datePart);
		appt.setTime(timePart);

		given()
		.header("Authorization","Bearer "+recipient1Token)
		.contentType("application/json")
		.body(appt)
		.when().post("/appointments")
		.then().statusCode(201);

		//Ensure visible for donor
		Integer apptid = given()
		.header("Authorization","Bearer "+donorToken)
		.when().get("/appointments")
		.then().statusCode(200)
		.body("$.size()",greaterThan(0))
		.extract().path("[0].id");


		//Donor completes appointment
		given()
		.header("Authorization","Bearer "+donorToken)
		.when().delete("/appointments/" + apptid + "/complete")
		.then().statusCode(200);

		


	}


}

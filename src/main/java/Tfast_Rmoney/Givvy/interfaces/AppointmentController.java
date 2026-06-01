package Tfast_Rmoney.Givvy.interfaces;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import Tfast_Rmoney.Givvy.entities.Appointment;
import Tfast_Rmoney.Givvy.interfaces.dtos.AppointmentDTO;
import Tfast_Rmoney.Givvy.interfaces.dtos.AppointmentWithDetails;
import Tfast_Rmoney.Givvy.security.AuctionUserDetails;
import Tfast_Rmoney.Givvy.security.WrongUserException;
import Tfast_Rmoney.Givvy.services.AppointmentService;

@RestController
@RequestMapping("/appointments")
@CrossOrigin(origins = "*")
public class AppointmentController {

    private AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @GetMapping(value = "/available-times", params = {"day", "locationId"})
    public ResponseEntity<List<LocalTime>> getAvailableTimes(@RequestParam("day") LocalDate day, @RequestParam("locationId") Integer locationId) {

        List<LocalTime> availableTimes = appointmentService.findAvailableTimes(day, locationId);
        return ResponseEntity.ok().body(availableTimes);
    }

    @PostMapping
    public ResponseEntity<String> createAppointment(Authentication authentication, @RequestBody AppointmentDTO appointment) {
        AuctionUserDetails details = (AuctionUserDetails) authentication.getPrincipal();
        int result = 0;

        try{
              result = appointmentService.saveAppointment(details.getUsername(), appointment);
        }
        catch(WrongUserException e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User not authorized to schedule appointment for this item");
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to schedule appointment");
        }
      
        if (result == -1) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid interest or location ID");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body("Appointment successfully scheduled");
    }


    // DELETE /appointments/{id}/complete
    // Complete the exchange, then remove appointment/schedules/item.
    @DeleteMapping("/{id}/complete")
    public ResponseEntity<String> completeAppointment(Authentication authentication, @PathVariable("id") Integer id) {
        AuctionUserDetails details = (AuctionUserDetails) authentication.getPrincipal();
        int result = 0;

        try{
            result = appointmentService.completeAndDeleteAppointment(details.getUsername(), id);
        } catch(WrongUserException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User not authorized to complete this appointment");
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to complete exchange");
        }

         if (result == -1) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Appointment not found with the provided ID");
        }


        return ResponseEntity.ok().body("Exchange completed and appointment/schedules/item removed");

    }


    @GetMapping("/{id}")
    public ResponseEntity<AppointmentDTO> getAppointment(Authentication authentication, @PathVariable("id") Integer id) {
        AuctionUserDetails details = (AuctionUserDetails) authentication.getPrincipal();

        AppointmentDTO appointment = null;
        try{
         appointment = appointmentService.getAppointmentById(details.getUsername(), id);
        } catch(WrongUserException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
         catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

        if (appointment == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        return ResponseEntity.ok().body(appointment);
    }

    // GET /appointments/{id}/details
    // Get appointment with full details including item and recipient info.
    @GetMapping("/{id}/details")
    public ResponseEntity<AppointmentWithDetails> getAppointmentWithDetails(Authentication authentication, @PathVariable("id") Integer id) {
        AuctionUserDetails details = (AuctionUserDetails) authentication.getPrincipal();


        try {
            AppointmentWithDetails apptDetails = appointmentService.getAppointmentDetails(details.getUsername(), id);
            if (apptDetails == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            return ResponseEntity.ok().body(apptDetails);
        } catch(WrongUserException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

    }

    @GetMapping
    public ResponseEntity<List<AppointmentDTO>> getAppointmentForUser(Authentication authentication) {
        AuctionUserDetails details = (AuctionUserDetails) authentication.getPrincipal();

        List<AppointmentDTO> appointments = appointmentService.getAppointmentsForUser(details.getUsername());

        if (appointments == null || appointments.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        return ResponseEntity.ok().body(appointments);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAppointment(Authentication authentication, @PathVariable("id") Integer id) {
        AuctionUserDetails details = (AuctionUserDetails) authentication.getPrincipal();

        int result = 0;
        try{
               result = appointmentService.cancelAppointment(details.getUsername(), id);
        }
        catch(WrongUserException e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User not authorized to cancel this appointment");
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to cancel appointment");
        }
        

        if (result == -1) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Appointment not found");
        }

        return ResponseEntity.ok().body("Appointment canceled and item moved back to pending");
    }
}
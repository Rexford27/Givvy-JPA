package Tfast_Rmoney.Givvy.interfaces;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import Tfast_Rmoney.Givvy.services.AppointmentService;

@RestController
@RequestMapping("/appointments")
@CrossOrigin(origins = "*")
public class AppointmentController {

    private AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @GetMapping(params = {"day", "locationId"})
    public ResponseEntity<List<LocalTime>> getAvailableTimes(
            @RequestParam("day") LocalDate day,
            @RequestParam("locationId") Integer locationId) {

        List<LocalTime> availableTimes = appointmentService.findAvailableTimes(day, locationId);
        return ResponseEntity.ok().body(availableTimes);
    }

    @PostMapping
    public ResponseEntity<String> createAppointment(@RequestBody AppointmentDTO appointment) {
        int result = 0;

        try{
              result = appointmentService.saveAppointment(appointment);
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
    public ResponseEntity<String> completeAppointment(@PathVariable Integer id) {

        int result = 0;

        try{
            result = appointmentService.completeAndDeleteAppointment(id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to complete exchange");
        }

         if (result == -1) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Appointment not found with the provided ID");
        }


        return ResponseEntity.ok().body("Exchange completed and appointment/schedules/item removed");

    }


    @GetMapping("/{id}")
    public ResponseEntity<AppointmentDTO> getAppointment(@PathVariable("id") Integer id) {
        AppointmentDTO appointment = appointmentService.getAppointmentById(id);

        if (appointment == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        return ResponseEntity.ok().body(appointment);
    }

    // GET /appointments/{id}/details
    // Get appointment with full details including item and recipient info.
    @GetMapping("/{id}/details")
    public ResponseEntity<AppointmentWithDetails> getAppointmentWithDetails(@PathVariable("id") Integer id) {
        AppointmentWithDetails apptDetails = appointmentService.getAppointmentDetails(id);

        if (apptDetails == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        return ResponseEntity.ok().body(apptDetails);
    }

    @GetMapping(params = {"userId"})
    public ResponseEntity<List<AppointmentDTO>> getAppointmentForUser(@RequestParam("userId") String userId) {
        List<AppointmentDTO> appointments = appointmentService.getAppointmentsForUser(userId);

        if (appointments == null || appointments.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        return ResponseEntity.ok().body(appointments);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAppointment(@PathVariable("id") Integer id) {

        int result = 0;
        try{
               result = appointmentService.cancelAppointment(id);
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
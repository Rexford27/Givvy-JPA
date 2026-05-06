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

import Tfast_Rmoney.Givvy.core.Appointment;
import Tfast_Rmoney.Givvy.core.AppointmentDAO;
import Tfast_Rmoney.Givvy.core.AppointmentWithDetails;

@RestController
@RequestMapping("/appointments")
@CrossOrigin(origins = "*")
public class AppointmentController {

    private AppointmentDAO appointmentDAO;

    public AppointmentController(AppointmentDAO appointmentDAO) {
        this.appointmentDAO = appointmentDAO;
    }

    @GetMapping(params = {"day", "locationId"})
    public ResponseEntity<List<LocalTime>> getAvailableTimes(
            @RequestParam("day") LocalDate day,
            @RequestParam("locationId") Integer locationId) {

        List<LocalTime> availableTimes = appointmentDAO.findAvailableTimes(day, locationId);
        return ResponseEntity.ok().body(availableTimes);
    }

    @PostMapping
    public ResponseEntity<String> createAppointment(@RequestBody Appointment appointment) {
        int createdAppointmentId = appointmentDAO.saveAppointment(appointment);

        if (createdAppointmentId == -1) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to schedule appointment");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body("Appointment successfully scheduled");
    }

    // DELETE /appointments/{id}/complete
    // Complete the exchange, then remove appointment/schedules/item.
    @DeleteMapping("/{id}/complete")
    public ResponseEntity<String> completeAppointment(@PathVariable Integer id) {
        int result = appointmentDAO.completeAndDeleteAppointment(id);

        if (result > 0) {
            return ResponseEntity.ok().body("Exchange completed and appointment/schedules/item removed");
        } else if (result == -1) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to complete exchange");
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Appointment not found");
    }


    @GetMapping("/{id}")
    public ResponseEntity<Appointment> getAppointment(@PathVariable("id") Integer id) {
        Appointment appointment = appointmentDAO.getAppointmentById(id);

        if (appointment == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        return ResponseEntity.ok().body(appointment);
    }

    // GET /appointments/{id}/details
    // Get appointment with full details including item and recipient info.
    @GetMapping("/{id}/details")
    public ResponseEntity<AppointmentWithDetails> getAppointmentWithDetails(@PathVariable("id") Integer id) {
        AppointmentWithDetails apptDetails = appointmentDAO.getAppointmentWithDetailsById(id);

        if (apptDetails == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        return ResponseEntity.ok().body(apptDetails);
    }

    @GetMapping(params = {"userId"})
    public ResponseEntity<List<Appointment>> getAppointmentForUser(@RequestParam("userId") String userId) {
        List<Appointment> appointments = appointmentDAO.getAppointmentForUser(userId);

        if (appointments == null || appointments.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        return ResponseEntity.ok().body(appointments);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAppointment(@PathVariable("id") Integer id) {
        int result = appointmentDAO.cancelAppointment(id);

        if (result == -1) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to cancel appointment");
        }

        if (result == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Appointment not found");
        }

        return ResponseEntity.ok().body("Appointment canceled and item moved back to pending");
    }
}
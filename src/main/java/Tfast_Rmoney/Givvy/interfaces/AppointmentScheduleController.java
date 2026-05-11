package Tfast_Rmoney.Givvy.interfaces;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import Tfast_Rmoney.Givvy.core.AppointmentSchedulingDAO;
import Tfast_Rmoney.Givvy.entities.AppointmentScheduling;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/appointmentschedules")
@CrossOrigin(origins = "*")
public class AppointmentScheduleController {

private AppointmentSchedulingDAO appointmentSchedulingDAO;


    public AppointmentScheduleController(AppointmentSchedulingDAO appointmentSchedulingDAO) {
        this.appointmentSchedulingDAO = appointmentSchedulingDAO;
    }

    @PostMapping
    public ResponseEntity<String> proposeAppointmentSchedules(@RequestBody List<AppointmentScheduling> potentialAppts) {

        for(AppointmentScheduling appt_sched : potentialAppts) {
            try{
                appointmentSchedulingDAO.proposeAppointment(appt_sched);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to propose appointment schedule");
            }

        }

        return ResponseEntity.ok().body("All appointment schedules proposed successfully");

    }


    @GetMapping(params = {"userId"})
    public ResponseEntity<List<AppointmentScheduling>> getAppointmentSchedulesByUserId(@RequestParam("userId") String userId) {
        List<AppointmentScheduling> appointmentSchedules = appointmentSchedulingDAO.getApptScheduleForUser(userId);
        return ResponseEntity.ok().body(appointmentSchedules);
    }


    @GetMapping(params = {"itemId"})
    public ResponseEntity<List<AppointmentScheduling>> getAppointmentSchedulesByItem(@RequestParam("itemId") String itemId) {
        List<AppointmentScheduling> appointmentSchedules = appointmentSchedulingDAO.getApptScheduleByItem(itemId);
        return ResponseEntity.ok().body(appointmentSchedules);
    }

    @DeleteMapping(params = {"scheduleId"})
    public ResponseEntity<String> deleteAppointmentSchedulesById(@RequestParam("scheduleId") Integer scheduleId) {
        try {
            appointmentSchedulingDAO.removeAppointmentScheduleById(scheduleId);
            return ResponseEntity.ok().body("Appointment schedule deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete appointment schedule");
        }

    }

    @DeleteMapping(params = {"itemId"})
    public ResponseEntity<String> deleteAppointmentSchedulesByItem(@RequestParam("itemId") String itemId) {
        try {
            appointmentSchedulingDAO.removeAppointmentScheduleByItemId(itemId);
            return ResponseEntity.ok().body("Appointment schedule deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete appointment schedule");
        }

    }


}
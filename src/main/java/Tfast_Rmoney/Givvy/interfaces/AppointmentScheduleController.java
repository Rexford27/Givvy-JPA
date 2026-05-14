package Tfast_Rmoney.Givvy.interfaces;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import Tfast_Rmoney.Givvy.entities.AppointmentScheduling;
import Tfast_Rmoney.Givvy.interfaces.dtos.AppointmentSchedulingDTO;
import Tfast_Rmoney.Givvy.services.AppointmentSchedulingService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/appointmentschedules")
@CrossOrigin(origins = "*")
public class AppointmentScheduleController {

private AppointmentSchedulingService appointmentSchedulingService;


    public AppointmentScheduleController(AppointmentSchedulingService appointmentSchedulingService) {
        this.appointmentSchedulingService = appointmentSchedulingService;
    }

    @PostMapping
    public ResponseEntity<String> proposeAppointmentSchedules(@RequestBody List<AppointmentSchedulingDTO> potentialAppts) {

        for(AppointmentSchedulingDTO appt_sched : potentialAppts) {
            try{
                appointmentSchedulingService.proposeAppointment(appt_sched);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to propose appointment schedule");
            }

        }

        return ResponseEntity.ok().body("All appointment schedules proposed successfully");

    }


    @GetMapping(params = {"userId"})
    public ResponseEntity<List<AppointmentSchedulingDTO>> getAppointmentSchedulesByUserId(@RequestParam("userId") UUID userId) {
        List<AppointmentSchedulingDTO> appointmentSchedules = appointmentSchedulingService.getApptSchedulesForUser(userId);
        return ResponseEntity.ok().body(appointmentSchedules);
    }


    @GetMapping(params = {"itemId"})
    public ResponseEntity<List<AppointmentSchedulingDTO>> getAppointmentSchedulesByItem(@RequestParam("itemId") UUID itemId) {
        List<AppointmentSchedulingDTO> appointmentSchedules = appointmentSchedulingService.getApptSchedulesByItem(itemId);
        return ResponseEntity.ok().body(appointmentSchedules);
    }

    @DeleteMapping(params = {"scheduleId"})
    public ResponseEntity<String> deleteAppointmentSchedulesById(@RequestParam("scheduleId") Integer scheduleId) {
        try {
            appointmentSchedulingService.removeAppointmentScheduleById(scheduleId);
            return ResponseEntity.ok().body("Appointment schedule deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete appointment schedule");
        }

    }

    @DeleteMapping(params = {"itemId"})
    public ResponseEntity<String> deleteAppointmentSchedulesByItem(@RequestParam("itemId") UUID itemId) {
        try {
            appointmentSchedulingService.removeAppointmentScheduleByItemId(itemId);
            return ResponseEntity.ok().body("Appointment schedule deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete appointment schedule");
        }

    }


}
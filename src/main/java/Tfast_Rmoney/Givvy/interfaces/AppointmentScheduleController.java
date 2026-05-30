package Tfast_Rmoney.Givvy.interfaces;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import Tfast_Rmoney.Givvy.entities.AppointmentScheduling;
import Tfast_Rmoney.Givvy.interfaces.dtos.AppointmentSchedulingDTO;
import Tfast_Rmoney.Givvy.security.AuctionUserDetails;
import Tfast_Rmoney.Givvy.security.WrongUserException;
import Tfast_Rmoney.Givvy.services.AppointmentSchedulingService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
    public ResponseEntity<String> proposeAppointmentSchedules(Authentication authentication, @RequestBody List<AppointmentSchedulingDTO> potentialAppts) {
        AuctionUserDetails details = (AuctionUserDetails) authentication.getPrincipal();

        int result = 0;
        for(AppointmentSchedulingDTO appt_sched : potentialAppts) {
            try{
                result = appointmentSchedulingService.proposeAppointment(details.getUsername(), appt_sched);
            } catch (WrongUserException e) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User is not authorized to propose appointment schedule for this interest");
            }
            catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to propose appointment schedule");
            }

        }

        if(result == -1) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid interest ID or location ID");
        }

        return ResponseEntity.ok().body("All appointment schedules proposed successfully");

    }


    @GetMapping
    public ResponseEntity<List<AppointmentSchedulingDTO>> getAppointmentSchedulesByUserId(Authentication authentication) {
        AuctionUserDetails details = (AuctionUserDetails) authentication.getPrincipal();
        List<AppointmentSchedulingDTO> appointmentSchedules = appointmentSchedulingService.getApptSchedulesForUser(details.getUsername());
        return ResponseEntity.ok().body(appointmentSchedules);
    }


    @GetMapping(params = {"itemId"})
    public ResponseEntity<List<AppointmentSchedulingDTO>> getAppointmentSchedulesByItem(Authentication authentication, @RequestParam("itemId") UUID itemId) {
        AuctionUserDetails details = (AuctionUserDetails) authentication.getPrincipal();

        try{
            List<AppointmentSchedulingDTO> appointmentSchedules = appointmentSchedulingService.getApptSchedulesByItem(details.getUsername(), itemId);
            return ResponseEntity.ok().body(appointmentSchedules);

        } catch (WrongUserException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    @DeleteMapping(params = {"scheduleId"})
    public ResponseEntity<String> deleteAppointmentSchedulesById(Authentication authentication, @RequestParam("scheduleId") Integer scheduleId) {
        AuctionUserDetails details = (AuctionUserDetails) authentication.getPrincipal();

        try {
            appointmentSchedulingService.removeAppointmentScheduleById(details.getUsername(), scheduleId);
            return ResponseEntity.ok().body("Appointment schedule deleted successfully");
        } catch (WrongUserException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User is not authorized to delete this appointment schedule");
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete appointment schedule");
        }

    }

    @DeleteMapping(params = {"itemId"})
    public ResponseEntity<String> deleteAppointmentSchedulesByItem(Authentication authentication, @RequestParam("itemId") UUID itemId) {
        AuctionUserDetails details = (AuctionUserDetails) authentication.getPrincipal();
        try {
            appointmentSchedulingService.removeAppointmentScheduleByItemId(details.getUsername(), itemId);
            return ResponseEntity.ok().body("Appointment schedule deleted successfully");
        } catch (WrongUserException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User is not authorized to delete this appointment schedule");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete appointment schedule");
        }

    }


}
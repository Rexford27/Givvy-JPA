package Tfast_Rmoney.Givvy.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import Tfast_Rmoney.Givvy.entities.Appointment;
import Tfast_Rmoney.Givvy.entities.AppointmentScheduling;

public interface AppointmentSchedulingRepository extends JpaRepository<AppointmentScheduling, Integer> {

    @Query("SELECT a FROM AppointmentScheduling a WHERE a.interest.user.userId = :userId")
    List<AppointmentScheduling> getApptSchedulesForUsers(String userId);

    @Query("SELECT a FROM AppointmentScheduling a WHERE a.interest.item.itemId = :itemId")
    List<AppointmentScheduling> getApptSchedulesByItem(Integer itemId);

    @Query("DELETE FROM AppointmentScheduling a WHERE a.appointment.id = :appointmentId")
    void deleteSchedulingsByItem(Integer itemId);

    
}
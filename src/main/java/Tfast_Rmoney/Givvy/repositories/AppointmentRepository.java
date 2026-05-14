package Tfast_Rmoney.Givvy.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import Tfast_Rmoney.Givvy.entities.Appointment;

public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {

    @Query("SELECT a FROM Appointment a WHERE a.day = :day AND a.location.id = :locationId")
    List<Appointment> getApptByDateAndLocation(LocalDate day, Integer locationId);
    
    @Query("SELECT a FROM Appointment a WHERE a.interest.user.userId = :userId OR a.interest.item.donor.userId = :userId")
    List<Appointment> getApptsForUsers(String userId);

    void deleteByInterestId(Integer interestId);

}
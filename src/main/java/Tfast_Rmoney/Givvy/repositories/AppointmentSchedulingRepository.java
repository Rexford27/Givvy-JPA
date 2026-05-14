package Tfast_Rmoney.Givvy.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import Tfast_Rmoney.Givvy.entities.AppointmentScheduling;

public interface AppointmentSchedulingRepository extends JpaRepository<AppointmentScheduling, Integer> {

    @Query("SELECT a FROM AppointmentScheduling a WHERE a.interest.user.userId = :userId")
    List<AppointmentScheduling> getApptSchedulesForUsers(UUID userId);

    @Query("SELECT a FROM AppointmentScheduling a WHERE a.interest.item.itemId = :itemId")
    List<AppointmentScheduling> getApptSchedulesByItem(UUID itemId);

    void deleteByInterestId(Integer interestId);

}
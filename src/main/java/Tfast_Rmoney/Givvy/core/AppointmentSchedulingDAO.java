package Tfast_Rmoney.Givvy.core;

import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

@Repository
public class AppointmentSchedulingDAO {

    private JdbcTemplate jdbcTemplate;

    public AppointmentSchedulingDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Loop here or in the controller?
    public int proposeAppointment(AppointmentScheduling appointment) {
        String sql = "INSERT INTO appt_scheduling (interest_id, location_id, start_time) VALUES (?, ?, ?)";
        try{
            jdbcTemplate.update(sql, appointment.getInterestId(), appointment.getLocationId(), appointment.getStartTime());
        } catch (Exception e) {
            return -1;
        }
        return 0;
    }

    public List<AppointmentScheduling> getApptScheduleForUser(String userId){
        String sql = """
            SELECT s.* FROM appt_scheduling s
            JOIN interest z ON s.interest_id = z.id
            WHERE z.user_id = ?
        """;
        RowMapper<AppointmentScheduling> rowMapper = new AppointmentSchedulingRowMapper();
        List<AppointmentScheduling> available_appointments = null;
        try{
            available_appointments = jdbcTemplate.query(sql, rowMapper, userId);
        } catch (Exception e) {
            available_appointments = new ArrayList<>();
        }

        return available_appointments;
    }

    public List<AppointmentScheduling> getApptScheduleByItem(String itemId){
        String sql = """
            SELECT s.* FROM appt_scheduling s
            JOIN interest z ON s.interest_id = z.id
            WHERE z.item_id = ?
        """;
        RowMapper<AppointmentScheduling> rowMapper = new AppointmentSchedulingRowMapper();
        List<AppointmentScheduling> available_appointments = null;
        try{
            available_appointments = jdbcTemplate.query(sql, rowMapper, itemId);
        } catch (Exception e) {
            available_appointments = new ArrayList<>();
        }

        return available_appointments;
    }


    public int removeAppointmentScheduleById(Integer appointmentSchedulingId){
        String sql = "DELETE FROM appt_scheduling WHERE id = ?";
        try{
            jdbcTemplate.update(sql, appointmentSchedulingId);
        } catch (Exception e) {
            return -1;
        }
        return 0;
    }

    public int removeAppointmentScheduleByItemId(String itemId){
        String sql = """
            DELETE FROM appt_scheduling 
            WHERE interest_id IN (
                SELECT id FROM interest WHERE item_id = ?
            )
        """;
        try{
            jdbcTemplate.update(sql, itemId);
        } catch (Exception e) {
            return -1;
        }
        return 0;
    }

}
package Tfast_Rmoney.Givvy.core;

import org.springframework.stereotype.Repository;

import Tfast_Rmoney.Givvy.entities.Appointment;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

@Repository
public class AppointmentDAO {

    private JdbcTemplate jdbcTemplate;
    private ItemDAO itemDao;

    public AppointmentDAO(JdbcTemplate jdbcTemplate, ItemDAO itemDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.itemDao = itemDao;
    }

    private static final List<LocalTime> BASE_TIME_SLOTS = generateBaseTimes();

    private static List<LocalTime> generateBaseTimes() {
        List<LocalTime> times = new ArrayList<>();
        LocalTime startTime = LocalTime.of(7, 0);
        LocalTime endTime = LocalTime.of(22, 0);
        LocalTime currentTime = startTime;

        while (!currentTime.isAfter(endTime)) {
            times.add(currentTime);
            currentTime = currentTime.plusMinutes(15);
        }
        return times;
    }

    public List<LocalTime> findAvailableTimes(LocalDate day, Integer locationId) {
        // Create a copy of the pre-computed time slots
        List<LocalTime> availableTimes = new ArrayList<>(BASE_TIME_SLOTS);

        // Query database for existing appointments on this day and location
        String sql = "SELECT time FROM appt WHERE day = ? AND location_id = ?";
        List<LocalTime> bookedTimes = null;
        try {
            bookedTimes = jdbcTemplate.queryForList(sql, LocalTime.class, day, locationId);
            // Filter out booked times from available times
            availableTimes.removeIf(bookedTimes::contains);
        } catch (Exception e) {
            // Handle exception
            availableTimes = null;
        }
        return availableTimes;
    }

    public int saveAppointment(Appointment appointment) {
        String sql = "INSERT INTO appt (interest_id, location_id, day, time) VALUES (?, ?, ?, ?)";
        try {
            jdbcTemplate.update(sql, appointment.getInterestId(), appointment.getLocationId(), appointment.getDay(), appointment.getTime());

            // Update item status to 'scheduled' when appointment is created
            String itemId = getItemIdByAppointment(appointment);
            if (itemId != null) {
                String itemSql = "UPDATE items SET status = ? WHERE itemId = ?";
                jdbcTemplate.update(itemSql, "scheduled", itemId);
            }
        } catch (Exception e) {
            return -1;
        }
        return 0;
    }

    // Helper method to get item_id from appointment
    private String getItemIdByAppointment(Appointment appointment) {
        try {
            return jdbcTemplate.queryForObject(
                "SELECT i.itemId FROM items i JOIN interest z ON i.itemId = z.item_id WHERE z.id = ?",
                String.class,
                appointment.getInterestId()
            );
        } catch (Exception e) {
            return null;
        }
    }

    public Appointment getAppointmentById(Integer id) {
        String sql = "SELECT * FROM appt WHERE id = ?";
        RowMapper<Appointment> rowMapper = new AppointmentRowMapper();
        Appointment appt = new Appointment();

        try {
            appt = jdbcTemplate.queryForObject(sql, rowMapper, id);
        } catch (Exception e) {
            // Handle exception
            appt = null;
        }

        return appt;
    }

    public AppointmentWithDetails getAppointmentWithDetailsById(Integer id) {
        String sql = "SELECT * FROM appointment_with_details WHERE id = ?";
        RowMapper<AppointmentWithDetails> rowMapper = new AppointmentWithDetailsRowMapper();

        try {
            return jdbcTemplate.queryForObject(sql, rowMapper, id);
        } catch (Exception e) {
            return null;
        }
    }

    public int cancelAppointment(Integer id) {
        // Get item_id before deleting the appointment
        String itemId = getItemIdByApptId(id);

        if (itemId == null) {
            return 0;
        }

        String sql = "DELETE FROM appt WHERE id = ?";
        try {
            int rows = jdbcTemplate.update(sql, id);

            if (rows == 0) {
                return 0;
            }

            // Update item status back to 'pending' when appointment is cancelled
            if (itemId != null) {
                String itemSql = "UPDATE items SET status = ? WHERE itemId = ?";
                jdbcTemplate.update(itemSql, "pending", itemId);
            }
        } catch (Exception e) {
            return -1;
        }

        return 1;
    }

    // Helper method to get item_id by appointment id
    private String getItemIdByApptId(Integer apptId) {
        try {
            return jdbcTemplate.queryForObject(
                "SELECT i.itemId FROM items i JOIN interest z ON i.itemId = z.item_id WHERE z.id = (SELECT interest_id FROM appt WHERE id = ?)",
                String.class,
                apptId
            );
        } catch (Exception e) {
            return null;
        }
    }

    // Complete and delete - removes appointment, schedules, and item
    public int completeAndDeleteAppointment(Integer id) {
        String itemId = getItemIdByApptId(id);

        if (itemId == null) {
            return 0; // Not found
        }

        try {
            jdbcTemplate.update("DELETE FROM appt WHERE id = ?", id);

            jdbcTemplate.update("""
                DELETE FROM appt_scheduling
                WHERE interest_id IN (
                    SELECT id FROM interest WHERE item_id = ?
                )
            """, itemId);

            int rows = itemDao.cancelItem(itemId);

            if (rows == -1) {
                return -1;
            }

            return rows;
        } catch (Exception e) {
            return -1;
        }
    }

    public List<Appointment> getAppointmentForUser(String userId) {
        String sql = """
            SELECT a.* FROM appt a
            JOIN interest z ON a.interest_id = z.id
            JOIN items i ON z.item_id = i.itemId
            WHERE z.user_id = ? OR i.donorId = ?
        """;
        RowMapper<Appointment> rowMapper = new AppointmentRowMapper();
        List<Appointment> appt = null;
        try {
            appt = jdbcTemplate.query(sql, rowMapper, userId, userId);
        } catch (Exception e) {
            // Handle exception
            appt = new ArrayList<>();
        }
        return appt;
    }
}
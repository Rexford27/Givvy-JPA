package Tfast_Rmoney.Givvy.core;

import org.springframework.jdbc.core.RowMapper;

import Tfast_Rmoney.Givvy.entities.AppointmentScheduling;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AppointmentSchedulingRowMapper implements RowMapper<AppointmentScheduling> {
    @Override
    public AppointmentScheduling mapRow(ResultSet rs, int rowNum) throws SQLException {
        AppointmentScheduling appt = new AppointmentScheduling();
        appt.setId(rs.getObject("id", Integer.class));
        appt.setInterestId(rs.getObject("interest_id", Integer.class));
        appt.setLocationId(rs.getObject("location_id", Integer.class));
        appt.setStartTime(rs.getTimestamp("start_time").toLocalDateTime());
        return appt;
    }
}
package Tfast_Rmoney.Givvy.core;

import org.springframework.jdbc.core.RowMapper;

import Tfast_Rmoney.Givvy.entities.Appointment;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AppointmentRowMapper implements RowMapper<Appointment> {
    @Override
    public Appointment mapRow(ResultSet rs, int rowNum) throws SQLException {
        Appointment appt = new Appointment();
        appt.setId(rs.getObject("id", Integer.class));
        appt.setInterestId(rs.getObject("interest_id", Integer.class));
        appt.setLocationId(rs.getObject("location_id", Integer.class));
        appt.setDay(rs.getDate("day").toLocalDate());
        appt.setTime(rs.getTime("time").toLocalTime());
        return appt;
    }
}
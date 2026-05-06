package Tfast_Rmoney.Givvy.core;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AppointmentWithDetailsRowMapper implements RowMapper<AppointmentWithDetails> {
    @Override
    public AppointmentWithDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
        AppointmentWithDetails appt = new AppointmentWithDetails();
        appt.setId(rs.getObject("id", Integer.class));
        appt.setInterestId(rs.getObject("interest_id", Integer.class));
        appt.setLocationId(rs.getObject("location_id", Integer.class));
        appt.setDay(rs.getDate("day").toLocalDate());
        appt.setTime(rs.getTime("time").toLocalTime());

        appt.setItemId(rs.getString("itemId"));
        appt.setItemTitle(rs.getString("itemTitle"));
        appt.setDonorId(rs.getString("donorId"));
        appt.setStatus(rs.getString("status"));

        appt.setUserId(rs.getString("userId"));
        appt.setUserName(rs.getString("userName"));
        appt.setUserEmail(rs.getString("userEmail"));
        
        return appt;
    }
}
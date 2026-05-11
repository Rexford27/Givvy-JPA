package Tfast_Rmoney.Givvy.core;

import org.springframework.jdbc.core.RowMapper;

import Tfast_Rmoney.Givvy.entities.Interest;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InterestRowMapper implements RowMapper<Interest> {
    @Override
    public Interest mapRow(ResultSet rs, int rowNum) throws SQLException {
        Interest interest = new Interest();
        interest.setId(rs.getObject("id", Integer.class));
        interest.setUserId(rs.getString("user_id"));
        interest.setItemId(rs.getString("item_id"));
        interest.setExpressedAt(rs.getTimestamp("expressed_at").toLocalDateTime());
        return interest;
    }
}
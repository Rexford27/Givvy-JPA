package Tfast_Rmoney.Givvy.core;
import java.sql.ResultSet; //brings in the sql 
import java.sql.SQLException; //handles sql error when theres error 

import org.jspecify.annotations.Nullable;
import org.springframework.jdbc.core.RowMapper; //row mapper

import Tfast_Rmoney.Givvy.entities.User;


public class UserRowMapper implements RowMapper {
    @Override
    public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
        User u = new User();
        u.setUserId(rs.getString("userId"));
        u.setName(rs.getString("name"));
        u.setEmail(rs.getString("email"));
        u.setPasswordHash(rs.getString("passwordHash"));
        u.setPhone(rs.getString("phone"));
        u.setCreatedAt(rs.getString("createdAt"));
        u.setSatisfactionScore(rs.getDouble("satisfactionScore"));
        
        return u;
    }
}
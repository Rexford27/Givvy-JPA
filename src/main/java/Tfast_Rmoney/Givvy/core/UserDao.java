package Tfast_Rmoney.Givvy.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

//importing the password system
import Tfast_Rmoney.Givvy.services.PasswordService;

@Repository
public class UserDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    //setting up the password system 
    @Autowired
    private PasswordService passwordService;

    public User findByEmail(String email) {
        //sql code for finding users where their id is a certain value
        String sql = "SELECT * FROM users WHERE email = ?";
        //row mapper to convert the retrieved information to java object
        RowMapper<User> rowMapper = new UserRowMapper();
        
        //user object returned y the jdbcTemplate 
        User result = null;
        
        //why is there try catch?
        //if the jdbcTemplate call is success full we save response in result
        try {
            result = jdbcTemplate.queryForObject(sql, rowMapper, email);
        } catch (Exception ex) {
            // no user found
        }

        return result;
    }

    public User findByEmailAndPassword(String email, String password) {
        // String sql = "SELECT * FROM users WHERE email = ?";
        // RowMapper<User> rowMapper = new UserRowMapper();

        // User result = null;

        // try {
        //     result = jdbcTemplate.queryForObject(sql, rowMapper, email);
        // } catch (Exception ex) {
        //     // no user found
        // }

        //finds the user by email 
        User result = findByEmail(email);
        //if the user exist we use password service to check if password = the same password
        if (result != null && passwordService.verifyHash(password, result.getPasswordHash())) {
            result.setPasswordHash("Undisclosed");
        } else {
            result = null;
        }

        return result;
    }

    public String save(User user) {
        //check duplicate email
        User old = findByEmail(user.getEmail());

        if (old != null) {
            return "user exist, duplicate";
        }
        //else user does not exisit and we save a new user
        // ask MySQL to generate UUID
        String idSQL = "SELECT UUID()";
        //what is a key?
        String key = null;

        try {
            key = jdbcTemplate.queryForObject(idSQL, String.class);
        } catch (Exception ex) {
            key = "Error";
        }

        if (key.equals("Error")) {
            return key;
        }

        // Step 3: hash password
        String hash = passwordService.hashPassword(user.getPasswordHash());

        // Step 4: insert user
        String insertSQL = """
            INSERT INTO users
            (userId, name, email, passwordHash, phone, createdAt, satisfactionScore)
            VALUES (?, ?, ?, ?, ?, NOW(), ?)
        """;

        jdbcTemplate.update(
            insertSQL,
            key,
            user.getName(),
            user.getEmail(),
            hash,
            user.getPhone(),
            0.0
        );

        return key;
    }
}
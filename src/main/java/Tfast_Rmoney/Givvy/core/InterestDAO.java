package Tfast_Rmoney.Givvy.core;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class InterestDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;


    public List<Interest> findByRecipientId(String recipientId) {
        String sql = "SELECT * FROM interest WHERE user_id=?";
        RowMapper<Interest> rowMapper = new InterestRowMapper();
       List<Interest> result = null;
        try {
            result = jdbcTemplate.query(sql, rowMapper, recipientId);
        } catch(Exception ex) {
            result = new ArrayList<>();
        }
        return result;	
    }


    public List<Interest> findByItemId(String itemId) {
        String sql = "SELECT * FROM interest WHERE item_id=?";
        RowMapper<Interest> rowMapper = new InterestRowMapper();
        List<Interest> result = null;
        try {
            result = jdbcTemplate.query(sql, rowMapper, itemId);
        } catch(Exception ex) {
            result = new ArrayList<>();
        }
        return result;	
    }

    public int expressInterest(Interest interest) {
        String sql = "INSERT INTO interest (user_id, item_id, expressed_at) VALUES (?, ?, ?)";
        int result = 0;
        try {
            result = jdbcTemplate.update(sql, interest.getUserId(), interest.getItemId(), interest.getExpressedAt());
        } catch(Exception ex) {
            
        }
        return result;	
    }

    // Helper to get item_id from interest
    private String getItemIdByInterestId(Integer interestId) {
        try {
            return jdbcTemplate.queryForObject(
                "SELECT item_id FROM interest WHERE id = ?",
                String.class,
                interestId
            );
        } catch (Exception e) {
            return null;
        }
    }

    // Helper to check if there are any accepted offers for this interest
    private boolean hasAcceptedOffer(Integer interestId) {
        try {
            Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM offers WHERE interest_id = ? AND status = 1",
                Integer.class,
                interestId
            );
            return count != null && count > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public void removeInterest(Integer interestId) {
        // Check if this interest has an accepted offer and item status is not 'available'
        boolean hasAccepted = hasAcceptedOffer(interestId);
        
        String sql = "DELETE FROM interest WHERE id=?";
        jdbcTemplate.update(sql, interestId);
        
        // If there was an accepted offer, set item status back to available
        if (hasAccepted) {
            String itemId = getItemIdByInterestId(interestId);
            if (itemId != null) {
                String itemSql = "UPDATE items SET status = ? WHERE itemId = ?";
                jdbcTemplate.update(itemSql, "available", itemId);
            }
        }
    }
}

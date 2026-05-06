package Tfast_Rmoney.Givvy.core;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class ItemDAO {

    private JdbcTemplate jdbcTemplate;

    public ItemDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public String save(Item item) {

        // Generate UUID from MySQL
        String idSQL = "SELECT UUID()";
        String key = null;

        try {
            // key holds the sql object for item 
            key = jdbcTemplate.queryForObject(idSQL, String.class);
        } catch (Exception ex) {
            // if key doesn't work then print error
            key = "Error";
        }
        //what is key quals

        if (key.equals("Error")) {
            return key;
        }

        String sql = """
            INSERT INTO items
            (itemId, donorId, title, description, imageUrl, status, postedAt)
            VALUES (?, ?, ?, ?, ?, ?, NOW())
        """;

        try {
            jdbcTemplate.update(
                sql,
                key,
                item.getDonorId(),
                item.getTitle(),
                item.getDescription(),
                item.getImageUrl(),
                "available"
            );
        } catch (Exception ex) {
            return "Error";
        }

        return key;
    }

    public Item findById(String itemId) {
        String sql = "SELECT * FROM items WHERE itemId = ?";
        RowMapper<Item> rowMapper = new ItemRowMapper();

        Item result = null;

        try {
            result = jdbcTemplate.queryForObject(sql, rowMapper, itemId);
        } catch (Exception ex) {
            // item not found
        }

        return result;
    }

    public List<Item> findByUser(String userId) {
        String sql = "SELECT * FROM items WHERE donorId = ?";
        RowMapper<Item> rowMapper = new ItemRowMapper();

        return jdbcTemplate.query(sql, rowMapper, userId);
    }

    public List<Item> findAll() {
        String sql = "SELECT * FROM items";
        RowMapper<Item> rowMapper = new ItemRowMapper();

        return jdbcTemplate.query(sql, rowMapper);
    }

    public int updateStatus(String itemId, String status) {
        String sql = "UPDATE items SET status = ? WHERE itemId = ?";
        try {
            return jdbcTemplate.update(sql, status, itemId);
        } catch (Exception e) {
            return -1;
        }
    }

    // Cancel item - triggers cascade delete via foreign key
    public int cancelItem(String itemId) {
        String sql = "DELETE FROM items WHERE itemId = ?";
        try {
            return jdbcTemplate.update(sql, itemId);
        } catch (Exception e) {
            return -1;
        }
    }
}
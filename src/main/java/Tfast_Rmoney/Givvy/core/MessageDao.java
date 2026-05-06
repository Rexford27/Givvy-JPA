package Tfast_Rmoney.Givvy.core;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class MessageDao {

    private JdbcTemplate jdbcTemplate;

    public MessageDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public String save(Message message) {
        String idSQL = "SELECT UUID()";
        String key = null;

        try {
            key = jdbcTemplate.queryForObject(idSQL, String.class);
        } catch (Exception ex) {
            return "Error";
        }

        String sql = """
            INSERT INTO messages
            (messageId, senderId, recipientId, itemId, subject, body, isRead, createdAt)
            VALUES (?, ?, ?, ?, ?, ?, false, NOW())
        """;

        try {
            jdbcTemplate.update(
                sql,
                key,
                message.getSenderId(),
                message.getRecipientId(),
                message.getItemId(),
                message.getSubject(),
                message.getBody()
            );
        } catch (Exception ex) {
            return "Error";
        }

        return key;
    }


    public String saveSystemMessage(String senderId, String recipientId, String itemId, String subject, String body) {
        Message message = new Message();

        message.setSenderId(senderId);
        message.setRecipientId(recipientId);
        message.setItemId(itemId);
        message.setSubject(subject);
        message.setBody(body);

        return save(message);
    }

    public List<Message> findByRecipientId(String recipientId) {
        String sql = "SELECT * FROM messages WHERE recipientId = ? ORDER BY createdAt DESC";
        RowMapper<Message> rowMapper = new MessageRowMapper();

        return jdbcTemplate.query(sql, rowMapper, recipientId);
    }

    public List<Message> findBySenderId(String senderId) {
        String sql = "SELECT * FROM messages WHERE senderId = ? ORDER BY createdAt DESC";
        RowMapper<Message> rowMapper = new MessageRowMapper();

        return jdbcTemplate.query(sql, rowMapper, senderId);
    }

    public int markAsRead(String messageId) {
        String sql = "UPDATE messages SET isRead = true WHERE messageId = ?";
        return jdbcTemplate.update(sql, messageId);
    }

    public int delete(String messageId) {
        String sql = "DELETE FROM messages WHERE messageId = ?";
        return jdbcTemplate.update(sql, messageId);
    }
}
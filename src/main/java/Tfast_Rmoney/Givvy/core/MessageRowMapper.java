package Tfast_Rmoney.Givvy.core;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class MessageRowMapper implements RowMapper<Message> {

    @Override
    public Message mapRow(ResultSet rs, int rowNum) throws SQLException {
        Message message = new Message();

        message.setMessageId(rs.getString("messageId"));
        message.setSenderId(rs.getString("senderId"));
        message.setRecipientId(rs.getString("recipientId"));
        message.setItemId(rs.getString("itemId"));
        message.setSubject(rs.getString("subject"));
        message.setBody(rs.getString("body"));
        message.setIsRead(rs.getBoolean("isRead"));

        if (rs.getTimestamp("createdAt") != null) {
            message.setCreatedAt(rs.getTimestamp("createdAt").toLocalDateTime());
        }

        return message;
    }
}
package Tfast_Rmoney.Givvy.interfaces.dtos;

import java.time.LocalDateTime;

import Tfast_Rmoney.Givvy.entities.Message;

public class MessageResponse {

    private String messageId;
    private String senderId;
    private String recipientId;
    private String itemId;
    private String subject;
    private String body;
    private Boolean isRead;
    private LocalDateTime createdAt;

    public MessageResponse(Message message) {
        this.messageId = message.getMessageId().toString();

        if (message.getSender() != null) {
            this.senderId = message.getSender().getUserId().toString();
        }

        if (message.getRecipient() != null) {
            this.recipientId = message.getRecipient().getUserId().toString();
        }

        if (message.getItem() != null) {
            this.itemId = message.getItem().getItemId().toString();
        }

        this.subject = message.getSubject();
        this.body = message.getBody();
        this.isRead = message.getIsRead();
        this.createdAt = message.getCreatedAt();
    }

    public String getMessageId() {
        return messageId;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getRecipientId() {
        return recipientId;
    }

    public String getItemId() {
        return itemId;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
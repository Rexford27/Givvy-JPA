package Tfast_Rmoney.Givvy.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import Tfast_Rmoney.Givvy.entities.Item;
import Tfast_Rmoney.Givvy.entities.Message;
import Tfast_Rmoney.Givvy.entities.User;
import Tfast_Rmoney.Givvy.repositories.ItemRepository;
import Tfast_Rmoney.Givvy.repositories.MessageRepository;
import Tfast_Rmoney.Givvy.repositories.UserRepository;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public MessageService(
            MessageRepository messageRepository,
            UserRepository userRepository,
            ItemRepository itemRepository) {

        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    public String saveMessage(Message message, UUID senderId, UUID recipientId, UUID itemId) {

        Optional<User> possibleSender = userRepository.findById(senderId);

        if (possibleSender.isEmpty()) {
            return "Invalid sender";
        }

        Optional<User> possibleRecipient = userRepository.findById(recipientId);

        if (possibleRecipient.isEmpty()) {
            return "Invalid recipient";
        }

        Optional<Item> possibleItem = itemRepository.findById(itemId);

        if (possibleItem.isEmpty()) {
            return "Invalid item";
        }

        User sender = possibleSender.get();
        User recipient = possibleRecipient.get();
        Item item = possibleItem.get();

        message.setSender(sender);
        message.setRecipient(recipient);
        message.setItem(item);
        message.setIsRead(false);

        Message savedMessage = messageRepository.save(message);

        return savedMessage.getMessageId().toString();
    }

    public String saveSystemMessage(
            UUID senderId,
            UUID recipientId,
            UUID itemId,
            String subject,
            String body) {

        Message message = new Message();

        message.setSubject(subject);
        message.setBody(body);

        return saveMessage(message, senderId, recipientId, itemId);
    }

    public List<Message> findMessagesByRecipient(UUID recipientId) {
        return messageRepository.findByRecipient_UserIdOrderByCreatedAtDesc(recipientId);
    }

    public List<Message> findMessagesBySender(UUID senderId) {
        return messageRepository.findBySender_UserIdOrderByCreatedAtDesc(senderId);
    }

    public Optional<Message> findMessageById(UUID messageId) {
        return messageRepository.findById(messageId);
    }

    public boolean markAsRead(UUID messageId) {

        Optional<Message> possibleMessage = messageRepository.findById(messageId);

        if (possibleMessage.isEmpty()) {
            return false;
        }

        Message message = possibleMessage.get();
        message.setIsRead(true);

        messageRepository.save(message);

        return true;
    }

    public int markAsRead(UUID messageId, UUID loggedInUserId) {

        // I use this secure version when the controller knows who is logged in.
        // Return meanings:
        // 1 = marked as read
        // -1 = message not found
        // -2 = logged-in user is not the recipient

        Optional<Message> possibleMessage = messageRepository.findById(messageId);

        if (possibleMessage.isEmpty()) {
            return -1;
        }

        Message message = possibleMessage.get();

        if (!isRecipient(message, loggedInUserId)) {
            return -2;
        }

        message.setIsRead(true);
        messageRepository.save(message);

        return 1;
    }

    public boolean deleteMessage(UUID messageId) {

        if (!messageRepository.existsById(messageId)) {
            return false;
        }

        messageRepository.deleteById(messageId);

        return true;
    }

    public int deleteMessage(UUID messageId, UUID loggedInUserId) {

        // I use this secure version when the controller knows who is logged in.
        // Return meanings:
        // 1 = deleted
        // -1 = message not found
        // -2 = logged-in user is not sender or recipient

        Optional<Message> possibleMessage = messageRepository.findById(messageId);

        if (possibleMessage.isEmpty()) {
            return -1;
        }

        Message message = possibleMessage.get();

        if (!isSender(message, loggedInUserId) && !isRecipient(message, loggedInUserId)) {
            return -2;
        }

        messageRepository.deleteById(messageId);

        return 1;
    }

    private boolean isSender(Message message, UUID userId) {

        if (message.getSender() == null || message.getSender().getUserId() == null) {
            return false;
        }

        return message.getSender().getUserId().equals(userId);
    }

    private boolean isRecipient(Message message, UUID userId) {

        if (message.getRecipient() == null || message.getRecipient().getUserId() == null) {
            return false;
        }

        return message.getRecipient().getUserId().equals(userId);
    }
}
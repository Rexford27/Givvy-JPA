package Tfast_Rmoney.Givvy.interfaces;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import Tfast_Rmoney.Givvy.entities.Message;
import Tfast_Rmoney.Givvy.interfaces.dtos.CreateMessageRequest;
import Tfast_Rmoney.Givvy.interfaces.dtos.MessageResponse;
import Tfast_Rmoney.Givvy.services.MessageService;

@RestController
@RequestMapping("/messages")
@CrossOrigin(origins = "*")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    // POST /messages
    @PostMapping
    public ResponseEntity<String> createMessage(@RequestBody CreateMessageRequest request) {

        if (request.getSenderId() == null || request.getSenderId().isBlank()
                || request.getRecipientId() == null || request.getRecipientId().isBlank()
                || request.getItemId() == null || request.getItemId().isBlank()
                || request.getBody() == null || request.getBody().isBlank()) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Missing senderId, recipientId, itemId, or body");
        }

        Optional<UUID> possibleSenderId = parseUuid(request.getSenderId());
        Optional<UUID> possibleRecipientId = parseUuid(request.getRecipientId());
        Optional<UUID> possibleItemId = parseUuid(request.getItemId());

        if (possibleSenderId.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Invalid senderId format");
        }

        if (possibleRecipientId.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Invalid recipientId format");
        }

        if (possibleItemId.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Invalid itemId format");
        }

        Message message = new Message();
        message.setSubject(request.getSubject());
        message.setBody(request.getBody());

        String key = messageService.saveMessage(
                message,
                possibleSenderId.get(),
                possibleRecipientId.get(),
                possibleItemId.get()
        );

        if (key.equals("Invalid sender")) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Sender user not found");
        }

        if (key.equals("Invalid recipient")) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Recipient user not found");
        }

        if (key.equals("Invalid item")) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Item not found");
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(key);
    }

    // GET /messages/recipient/{userId}
    @GetMapping("/recipient/{userId}")
    public ResponseEntity<List<MessageResponse>> getMessagesForRecipient(@PathVariable String userId) {

        Optional<UUID> possibleUserId = parseUuid(userId);

        if (possibleUserId.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }

        List<Message> messages = messageService.findMessagesByRecipient(possibleUserId.get());
        List<MessageResponse> response = convertToResponseList(messages);

        return ResponseEntity.ok(response);
    }

    // GET /messages/sender/{userId}
    @GetMapping("/sender/{userId}")
    public ResponseEntity<List<MessageResponse>> getMessagesFromSender(@PathVariable String userId) {

        Optional<UUID> possibleUserId = parseUuid(userId);

        if (possibleUserId.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }

        List<Message> messages = messageService.findMessagesBySender(possibleUserId.get());
        List<MessageResponse> response = convertToResponseList(messages);

        return ResponseEntity.ok(response);
    }

    // PATCH /messages/{messageId}/read
    @PatchMapping("/{messageId}/read")
    public ResponseEntity<String> markAsRead(@PathVariable String messageId) {

        Optional<UUID> possibleMessageId = parseUuid(messageId);

        if (possibleMessageId.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Invalid messageId format");
        }

        boolean updated = messageService.markAsRead(possibleMessageId.get());

        if (!updated) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Message not found");
        }

        return ResponseEntity.ok("Message marked as read");
    }

    // DELETE /messages/{messageId}
    @DeleteMapping("/{messageId}")
    public ResponseEntity<String> deleteMessage(@PathVariable String messageId) {

        Optional<UUID> possibleMessageId = parseUuid(messageId);

        if (possibleMessageId.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Invalid messageId format");
        }

        boolean deleted = messageService.deleteMessage(possibleMessageId.get());

        if (!deleted) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Message not found");
        }

        return ResponseEntity.ok("Message deleted");
    }

    private Optional<UUID> parseUuid(String value) {
        try {
            return Optional.of(UUID.fromString(value));
        } catch (Exception ex) {
            return Optional.empty();
        }
    }

    private List<MessageResponse> convertToResponseList(List<Message> messages) {
        List<MessageResponse> response = new ArrayList<>();

        for (Message message : messages) {
            response.add(new MessageResponse(message));
        }

        return response;
    }
}
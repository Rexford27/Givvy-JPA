package Tfast_Rmoney.Givvy.interfaces;

import java.util.List;

import Tfast_Rmoney.Givvy.core.Message;
import Tfast_Rmoney.Givvy.core.MessageDao;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/messages")
@CrossOrigin(origins = "*")
public class MessageController {

    private MessageDao dao;

    public MessageController(MessageDao dao) {
        this.dao = dao;
    }

    // POST /messages
    @PostMapping
    public ResponseEntity<String> createMessage(@RequestBody Message message) {

        if (message.getSenderId() == null || message.getSenderId().isBlank()
                || message.getRecipientId() == null || message.getRecipientId().isBlank()
                || message.getBody() == null || message.getBody().isBlank()) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Missing senderId, recipientId, or body");
        }

        String key = dao.save(message);

        if (key.equals("Error")) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Could not create message");
        }

        return ResponseEntity.ok(key);
    }

    // GET /messages/recipient/{userId}
    @GetMapping("/recipient/{userId}")
    public ResponseEntity<List<Message>> getMessagesForRecipient(@PathVariable String userId) {
        return ResponseEntity.ok(dao.findByRecipientId(userId));
    }

    // GET /messages/sender/{userId}
    @GetMapping("/sender/{userId}")
    public ResponseEntity<List<Message>> getMessagesFromSender(@PathVariable String userId) {
        return ResponseEntity.ok(dao.findBySenderId(userId));
    }

    // PATCH /messages/{messageId}/read
    @PatchMapping("/{messageId}/read")
    public ResponseEntity<String> markAsRead(@PathVariable String messageId) {
        int rows = dao.markAsRead(messageId);

        if (rows == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Message not found");
        }

        return ResponseEntity.ok("Message marked as read");
    }

    // DELETE /messages/{messageId}
    @DeleteMapping("/{messageId}")
    public ResponseEntity<String> deleteMessage(@PathVariable String messageId) {
        int rows = dao.delete(messageId);

        if (rows == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Message not found");
        }

        return ResponseEntity.ok("Message deleted");
    }
}
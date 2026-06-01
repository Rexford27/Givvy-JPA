package Tfast_Rmoney.Givvy.interfaces;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import Tfast_Rmoney.Givvy.entities.Message;
import Tfast_Rmoney.Givvy.interfaces.dtos.CreateMessageRequest;
import Tfast_Rmoney.Givvy.interfaces.dtos.MessageResponse;
import Tfast_Rmoney.Givvy.security.AuctionUserDetails;
import Tfast_Rmoney.Givvy.services.MessageService;

@RestController
@RequestMapping("/messages")
@CrossOrigin(origins = "*")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping
    public ResponseEntity<String> createMessage(
            Authentication authentication,
            @RequestBody CreateMessageRequest request
    ) {

        // I use Authentication here because the sender should come from the JWT.
        // I do not trust request.getSenderId() because someone could fake another sender.

        if (request.getRecipientId() == null || request.getRecipientId().isBlank()
                || request.getItemId() == null || request.getItemId().isBlank()
                || request.getBody() == null || request.getBody().isBlank()) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Missing recipientId, itemId, or body");
        }

        UUID loggedInUserId = getLoggedInUserId(authentication);

        Optional<UUID> possibleRecipientId = parseUuid(request.getRecipientId());
        Optional<UUID> possibleItemId = parseUuid(request.getItemId());

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
                loggedInUserId,
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

    @GetMapping("/recipient/{userId}")
    public ResponseEntity<List<MessageResponse>> getMessagesForRecipient(
            Authentication authentication,
            @PathVariable String userId
    ) {

        // I keep the same route, but the URL userId must match the JWT userId.

        Optional<UUID> possibleUserId = parseUuid(userId);

        if (possibleUserId.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }

        UUID loggedInUserId = getLoggedInUserId(authentication);

        if (!loggedInUserId.equals(possibleUserId.get())) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .build();
        }

        List<Message> messages = messageService.findMessagesByRecipient(possibleUserId.get());
        List<MessageResponse> response = convertToResponseList(messages);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/sender/{userId}")
    public ResponseEntity<List<MessageResponse>> getMessagesFromSender(
            Authentication authentication,
            @PathVariable String userId
    ) {

        // I keep the same route, but the URL userId must match the JWT userId.

        Optional<UUID> possibleUserId = parseUuid(userId);

        if (possibleUserId.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }

        UUID loggedInUserId = getLoggedInUserId(authentication);

        if (!loggedInUserId.equals(possibleUserId.get())) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .build();
        }

        List<Message> messages = messageService.findMessagesBySender(possibleUserId.get());
        List<MessageResponse> response = convertToResponseList(messages);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{messageId}/read")
    public ResponseEntity<String> markAsRead(
            Authentication authentication,
            @PathVariable String messageId
    ) {

        // I only allow the recipient of the message to mark it as read.

        Optional<UUID> possibleMessageId = parseUuid(messageId);

        if (possibleMessageId.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Invalid messageId format");
        }

        UUID loggedInUserId = getLoggedInUserId(authentication);

        int result = messageService.markAsRead(possibleMessageId.get(), loggedInUserId);

        if (result == -1) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Message not found");
        }

        if (result == -2) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("Only the recipient can mark this message as read");
        }

        return ResponseEntity.ok("Message marked as read");
    }

    @DeleteMapping("/{messageId}")
    public ResponseEntity<String> deleteMessage(
            Authentication authentication,
            @PathVariable String messageId
    ) {

        // I only allow the sender or recipient to delete a message.

        Optional<UUID> possibleMessageId = parseUuid(messageId);

        if (possibleMessageId.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Invalid messageId format");
        }

        UUID loggedInUserId = getLoggedInUserId(authentication);

        int result = messageService.deleteMessage(possibleMessageId.get(), loggedInUserId);

        if (result == -1) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Message not found");
        }

        if (result == -2) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("Only the sender or recipient can delete this message");
        }

        return ResponseEntity.ok("Message deleted");
    }

    private UUID getLoggedInUserId(Authentication authentication) {

        // I pull the logged-in user's identity out of the Authentication object.
        // In this project, the principal is an AuctionUserDetails object.

        AuctionUserDetails details = (AuctionUserDetails) authentication.getPrincipal();

        return UUID.fromString(details.getUsername());
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
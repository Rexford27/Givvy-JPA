package Tfast_Rmoney.Givvy.interfaces;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import Tfast_Rmoney.Givvy.entities.Item;
import Tfast_Rmoney.Givvy.interfaces.dtos.CreateItemRequest;
import Tfast_Rmoney.Givvy.interfaces.dtos.ItemResponse;
import Tfast_Rmoney.Givvy.security.AuctionUserDetails;
import Tfast_Rmoney.Givvy.services.ItemService;

@RestController
@RequestMapping("/items")
@CrossOrigin(origins = "*")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }


    @GetMapping("/donor")
    public ResponseEntity<List<ItemResponse>> getItemsByDonor(Authentication authentication){
        AuctionUserDetails details = (AuctionUserDetails) authentication.getPrincipal();
        List<Item> items = itemService.findItemsByUser(UUID.fromString(details.getUsername()));

        List<ItemResponse> itemResponses = new ArrayList<>();
        for (Item item : items) {
            itemResponses.add(new ItemResponse(item));
        }

        return ResponseEntity.ok(itemResponses);
    }
    
    @PostMapping
    public ResponseEntity<String> createItem(
            Authentication authentication,
            @RequestBody CreateItemRequest request
    ) {

        // I use Authentication here because the donor should come from the JWT.
        // I do not trust request.getDonorId() because someone could fake another user's id.

        if (request.getTitle() == null || request.getTitle().isBlank()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Missing title");
        }

        UUID loggedInUserId = getLoggedInUserId(authentication);

        Item item = new Item();
        item.setTitle(request.getTitle());
        item.setDescription(request.getDescription());
        item.setImageUrl(request.getImageUrl());

        String itemId = itemService.saveItem(item, loggedInUserId);

        if (itemId.equals("Invalid donor")) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Donor user not found");
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(itemId);
    }

    @GetMapping
    public ResponseEntity<List<ItemResponse>> getItems(
            @RequestParam(required = false) String status
    ) {

        // I added this list endpoint so the REST Assured test can prove
        // that a newly posted item appears in the offered/available item list.

        List<Item> items;

        if (status == null || status.isBlank()) {
            items = itemService.findAllItems();
        } else {
            items = itemService.findItemsByStatus(status);
        }

        List<ItemResponse> response = convertToResponseList(items);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemResponse> getItemById(@PathVariable String itemId) {

        Optional<UUID> possibleItemId = parseUuid(itemId);

        if (possibleItemId.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }

        Optional<Item> possibleItem = itemService.findItemById(possibleItemId.get());

        if (possibleItem.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }

        ItemResponse response = new ItemResponse(possibleItem.get());

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{itemId}/status")
    public ResponseEntity<String> updateStatus(
            Authentication authentication,
            @PathVariable String itemId,
            @RequestBody Map<String, String> body
    ) {

        // I secure this route so only the donor who owns the item can update its status.

        Optional<UUID> possibleItemId = parseUuid(itemId);

        if (possibleItemId.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Invalid itemId format");
        }

        String status = body.get("status");

        if (status == null || status.isBlank()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Missing status");
        }

        UUID loggedInUserId = getLoggedInUserId(authentication);

        int result = itemService.updateStatus(possibleItemId.get(), loggedInUserId, status);

        if (result == -1) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Item not found");
        }

        if (result == -2) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("You can only update your own item");
        }

        return ResponseEntity.ok("Status updated");
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<String> cancelItem(
            Authentication authentication,
            @PathVariable String itemId
    ) {

        // I secure this route so only the donor who owns the item can cancel/delete it.

        Optional<UUID> possibleItemId = parseUuid(itemId);

        if (possibleItemId.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Invalid itemId format");
        }

        UUID loggedInUserId = getLoggedInUserId(authentication);

        int result = itemService.cancelItem(possibleItemId.get(), loggedInUserId);

        if (result == -1) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Item not found");
        }

        if (result == -2) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("You can only cancel your own item");
        }

        return ResponseEntity.ok("Item/offer cancelled successfully");
    }

    private UUID getLoggedInUserId(Authentication authentication) {

        // I pull the logged-in user's identity out of the Authentication object.
        // In this project, the principal is an AuctionUserDetails object.

        AuctionUserDetails details = (AuctionUserDetails) authentication.getPrincipal();

        // In this project, getUsername() stores the user's UUID as a String.

        return UUID.fromString(details.getUsername());
    }

    private Optional<UUID> parseUuid(String value) {
        try {
            return Optional.of(UUID.fromString(value));
        } catch (Exception ex) {
            return Optional.empty();
        }
    }

    private List<ItemResponse> convertToResponseList(List<Item> items) {
        List<ItemResponse> response = new ArrayList<>();

        for (Item item : items) {
            response.add(new ItemResponse(item));
        }

        return response;
    }
}

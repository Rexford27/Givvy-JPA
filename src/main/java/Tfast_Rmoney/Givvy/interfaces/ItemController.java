package Tfast_Rmoney.Givvy.interfaces;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import Tfast_Rmoney.Givvy.core.InterestDAO;
import Tfast_Rmoney.Givvy.entities.Interest;
import Tfast_Rmoney.Givvy.entities.Item;
import Tfast_Rmoney.Givvy.interfaces.dtos.CreateItemRequest;
import Tfast_Rmoney.Givvy.interfaces.dtos.ItemResponse;
import Tfast_Rmoney.Givvy.services.ItemService;

@RestController
@RequestMapping("/items")
@CrossOrigin(origins = "*")
public class ItemController {

    private final ItemService itemService;
    private final InterestDAO interestDao;

    public ItemController(ItemService itemService, InterestDAO interestDao) {
        this.itemService = itemService;
        this.interestDao = interestDao;
    }

    // POST /items
    @PostMapping
    public ResponseEntity<String> createItem(@RequestBody CreateItemRequest request) {

        if (request.getDonorId() == null || request.getDonorId().isBlank()
                || request.getTitle() == null || request.getTitle().isBlank()) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Missing donorId or title");
        }

        Optional<UUID> possibleDonorId = parseUuid(request.getDonorId());

        if (possibleDonorId.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Invalid donorId format");
        }

        Item item = new Item();
        item.setTitle(request.getTitle());
        item.setDescription(request.getDescription());
        item.setImageUrl(request.getImageUrl());

        String itemId = itemService.saveItem(item, possibleDonorId.get());

        if (itemId.equals("Invalid donor")) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Donor user not found");
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(itemId);
    }

    // GET /items/{itemId}
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

    // PATCH /items/{itemId}/status
    @PatchMapping("/{itemId}/status")
    public ResponseEntity<String> updateStatus(
            @PathVariable String itemId,
            @RequestBody Map<String, String> body) {

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

        boolean updated = itemService.updateStatus(possibleItemId.get(), status);

        if (!updated) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Item not found");
        }

        return ResponseEntity.ok("Status updated");
    }

    // GET /items/{itemid}/interests
    @GetMapping("/{itemid}/interests")
    public ResponseEntity<List<Interest>> findInterestsForItem(@PathVariable String itemid) {

        Optional<UUID> possibleItemId = parseUuid(itemid);

        if (possibleItemId.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }

        if (itemService.findItemById(possibleItemId.get()).isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }

        List<Interest> results = interestDao.findByItemId(itemid);

        return ResponseEntity.ok().body(results);
    }

    // POST /items/{itemid}/interests
    @PostMapping("/{itemid}/interests")
    public ResponseEntity<String> expressInterest(
            @PathVariable String itemid,
            @RequestBody Interest interest) {

        Optional<UUID> possibleItemId = parseUuid(itemid);

        if (possibleItemId.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Invalid itemId format");
        }

        if (itemService.findItemById(possibleItemId.get()).isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Item not found");
        }

        interest.setItemId(itemid);

        int result = interestDao.expressInterest(interest);

        if (result == 0) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error expressing interest");
        }

        return ResponseEntity.ok().body("Interest expressed successfully");
    }

    // DELETE /items/interests/{interestId}
    @DeleteMapping("/interests/{interestId}")
    public ResponseEntity<String> removeInterest(@PathVariable int interestId) {

        interestDao.removeInterest(interestId);

        return ResponseEntity.ok().body("Interest removed successfully");
    }

    // DELETE /items/{itemId}
    @DeleteMapping("/{itemId}")
    public ResponseEntity<String> cancelItem(@PathVariable String itemId) {

        Optional<UUID> possibleItemId = parseUuid(itemId);

        if (possibleItemId.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Invalid itemId format");
        }

        boolean deleted = itemService.cancelItem(possibleItemId.get());

        if (!deleted) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Item not found");
        }

        return ResponseEntity.ok().body("Item/offer cancelled successfully");
    }

    private Optional<UUID> parseUuid(String value) {
        try {
            return Optional.of(UUID.fromString(value));
        } catch (Exception ex) {
            return Optional.empty();
        }
    }
}
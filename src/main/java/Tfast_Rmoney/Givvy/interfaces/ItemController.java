package Tfast_Rmoney.Givvy.interfaces;

import java.util.List;
import java.util.Map;

import Tfast_Rmoney.Givvy.core.Interest;
import Tfast_Rmoney.Givvy.core.InterestDAO;
import Tfast_Rmoney.Givvy.core.Item;
import Tfast_Rmoney.Givvy.core.ItemDAO;
import Tfast_Rmoney.Givvy.core.InterestDAO;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/items")
@CrossOrigin(origins = "*")
public class ItemController {

    private ItemDAO itemDao;
    private InterestDAO interestDao;

    public ItemController(ItemDAO itemdao, InterestDAO interestDao) {
        this.itemDao = itemdao;
        this.interestDao = interestDao;
    }

    // POST /items
    @PostMapping
    public ResponseEntity<String> createItem(@RequestBody Item item) {

        if (item.getDonorId() == null || item.getDonorId().isBlank()
                || item.getTitle() == null || item.getTitle().isBlank()) {
    //returning a response entity emplying that somthing is missing 
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Missing donorId or title");
        }

        String itemId = itemDao.save(item);
//if we get another error we send the response entity to say we could not create item
        if (itemId.equals("Error")) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Could not create item");
        }

        return ResponseEntity.ok(itemId);
    }

    // GET /items/{itemId}
    @GetMapping("/{itemId}")
    public ResponseEntity<Item> getItemById(@PathVariable String itemId) {
        Item item = itemDao.findById(itemId);

        if (item == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
//returns the item in a jackson formatated jason 
        return ResponseEntity.ok(item);
    }

    //to get all the items by a user go to user controller 
    // // GET /users/{userId}/items
    // @GetMapping("/users/{userId}/items")
    // public ResponseEntity<List<Item>> getItemsByUser(@PathVariable String userId) {
    //     return ResponseEntity.ok(itemDao.findByUser(userId));
    // }

    // PATCH /items/{itemId}/status
    @PatchMapping("/{itemId}/status")
    public ResponseEntity<String> updateStatus(
            @PathVariable String itemId,
            @RequestBody Map<String, String> body) {

        String status = body.get("status");

        if (status == null || status.isBlank()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Missing status");
        }

        int rows = itemDao.updateStatus(itemId, status);

        if (rows == 0) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Item not found");
        }

        return ResponseEntity.ok("Status updated");
    }

    ////// check in with T-fast to see if it's compataible with his code 

    @GetMapping("/{itemid}/interests")
    public ResponseEntity<List<Interest>> findInterestsForItem(@PathVariable String itemid) {
        List<Interest> results = interestDao.findByItemId(itemid);
        return ResponseEntity.ok().body(results);
    }
    
    @PostMapping("/{itemid}/interests")
    public ResponseEntity<String> expressInterest(@PathVariable String itemid, @RequestBody Interest interest) {
        interest.setItemId(itemid);
        int result = interestDao.expressInterest(interest);
        if (result == 0) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error expressing interest");
        }
        return ResponseEntity.ok().body("Interest expressed successfully");
    }

    @DeleteMapping("/interests/{interestId}")
    public ResponseEntity<String> removeInterest(@PathVariable int id, @PathVariable int interestId) {
        interestDao.removeInterest(interestId);
        return ResponseEntity.ok().body("Interest removed successfully");
    }

    // DELETE /items/{itemId} - Cancel item (donor withdraws offer)
    // Triggers cascade delete via foreign key constraints
    @DeleteMapping("/{itemId}")
    public ResponseEntity<String> cancelItem(@PathVariable String itemId) {
        int result = itemDao.cancelItem(itemId);
        if (result > 0) {
            return ResponseEntity.ok().body("Item/offer cancelled successfully");
        } else if (result == -1) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to cancel item");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Item not found");
    }
}

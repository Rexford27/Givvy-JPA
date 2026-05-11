package Tfast_Rmoney.Givvy.interfaces;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import Tfast_Rmoney.Givvy.core.InterestDAO;
import Tfast_Rmoney.Givvy.core.OfferDAO;
import Tfast_Rmoney.Givvy.entities.Interest;
import Tfast_Rmoney.Givvy.entities.Offer;
import Tfast_Rmoney.Givvy.interfaces.dtos.OfferResponse;


@RestController
@RequestMapping("/interests")
@CrossOrigin(origins = "*")
public class InterestController {


    private InterestDAO interestDao;
    private OfferDAO offerDao;

    public InterestController(InterestDAO interestDao, OfferDAO offerDao) {
        this.interestDao = interestDao;
        this.offerDao = offerDao;
    }

    @GetMapping(params = {"itemid"})
    public ResponseEntity<List<Interest>> findInterestsForItem(@RequestParam("itemid") String itemid) {
        List<Interest> results = interestDao.findByItemId(itemid);
        return ResponseEntity.ok().body(results);
    }
    
    @PostMapping
    public ResponseEntity<String> expressInterest(@RequestBody Interest interest) {
        int result = interestDao.expressInterest(interest);
        if (result == 0) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error expressing interest");
        }
        return ResponseEntity.ok().body("Interest expressed successfully");
    }

    @DeleteMapping("/{interestId}")
    public ResponseEntity<String> removeInterest(@PathVariable Integer interestId) {
        interestDao.removeInterest(interestId);
        return ResponseEntity.ok().body("Interest removed successfully");
    }
    

    @GetMapping(params = {"userId"})
    public ResponseEntity<List<Interest>> findInterestsForUser(@RequestParam("userId") String userId) {
        List<Interest> results = interestDao.findByRecipientId(userId);
        return ResponseEntity.ok().body(results);
    }

    @GetMapping(value ="/offers", params = {"recipientId"})
    public ResponseEntity<List<Offer>> getOffersForRecipient(@RequestParam("recipientId") String recipientId) {
        List<Offer> offers = offerDao.getOffersForRecipient(recipientId);
        return ResponseEntity.ok().body(offers);
    }

    @GetMapping(value = "/offers",params = {"donorId"})
    public ResponseEntity<List<Offer>> getOffersForDonor(@RequestParam("donorId") String donorId) {
        List<Offer> offers = offerDao.getOffersForDonor(donorId);
        return ResponseEntity.ok().body(offers);
    }

    @PostMapping("/offers")
    public ResponseEntity<String> saveOffer(@RequestBody Offer offer) {
        int result = offerDao.saveOffer(offer);
        if (result == 0) {
            return ResponseEntity.status(HttpStatus.CREATED).body("Offer saved successfully");
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save offer");
    }

    @DeleteMapping("/offers/{offerId}")
    public ResponseEntity<String> deleteOffer(@PathVariable String offerId) {
        int result = offerDao.deleteOffer(offerId);
        if (result == 0) {
            return ResponseEntity.ok().body("Offer deleted successfully");
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete offer");
    }

    @PostMapping("/offerresponse")
    public ResponseEntity<String> updateOffer(@RequestBody OfferResponse offerRes) {
        int result = offerDao.updateOffer(offerRes);
        if (result == 0) {
            return ResponseEntity.ok().body("Offer updated successfully");
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update offer");
    }
}

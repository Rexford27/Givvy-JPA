package Tfast_Rmoney.Givvy.interfaces;

import java.util.List;
import java.util.UUID;

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


import Tfast_Rmoney.Givvy.entities.Interest;
import Tfast_Rmoney.Givvy.entities.Offer;
import Tfast_Rmoney.Givvy.interfaces.dtos.InterestDTO;
import Tfast_Rmoney.Givvy.interfaces.dtos.OfferDTO;
import Tfast_Rmoney.Givvy.interfaces.dtos.OfferResponse;
import Tfast_Rmoney.Givvy.services.InterestService;


@RestController
@RequestMapping("/interests")
@CrossOrigin(origins = "*")
public class InterestController {


    private InterestService interestService;

    public InterestController(InterestService interestService) {
        this.interestService = interestService;
    }

    @GetMapping(params = {"itemid"})
    public ResponseEntity<List<InterestDTO>> findInterestsForItem(@RequestParam("itemid") UUID itemid) {
        
        List<InterestDTO> results = interestService.getInterestsByItemId(itemid);

        if(results.size() == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        
        return ResponseEntity.ok().body(results);
    }
    
    @PostMapping
    public ResponseEntity<String> expressInterest(@RequestBody InterestDTO interest) {
        int result = 0;

        try {
            result = interestService.expressInterest(interest);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error expressing interest");
        }

        if (result == -1) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Item or User not found");
        }

        return ResponseEntity.ok().body("Interest expressed successfully");
    }

    
    @DeleteMapping("/{interestId}")
    public ResponseEntity<String> removeInterest(@PathVariable Integer interestId) {
        int result = 0;

        try {
            result = interestService.deleteInterest(interestId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error removing interest");
        }

        if (result == -1) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Interest not found");
        }

        return ResponseEntity.ok().body("Interest removed successfully");
    }
    

    @GetMapping(params = {"userId"})
    public ResponseEntity<List<InterestDTO>> findInterestsForUser(@RequestParam("userId") UUID userId) {
        List<InterestDTO> results = interestService.getInterestsByRecipientId(userId);
        return ResponseEntity.ok().body(results);
    }

    @GetMapping(value ="/offers", params = {"recipientId"})
    public ResponseEntity<List<OfferDTO>> getOffersForRecipient(@RequestParam("recipientId") UUID recipientId) {
        List<OfferDTO> offers = interestService.getOffersByRecipientId(recipientId);
        return ResponseEntity.ok().body(offers);
    }

    @GetMapping(value = "/offers",params = {"donorId"})
    public ResponseEntity<List<OfferDTO>> getOffersForDonor(@RequestParam("donorId") UUID donorId) {
        List<OfferDTO> offers = interestService.getOffersByDonorId(donorId);
        return ResponseEntity.ok().body(offers);
    }

    @PostMapping("/offers")
    public ResponseEntity<String> saveOffer(@RequestBody OfferDTO offer) {
        int result = 0;

        try {
            result = interestService.saveOffer(offer);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving offer");
        }

        if (result == -1) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No associated interest found");
        }

        return ResponseEntity.ok().body("Offer saved successfully");
    }

    @DeleteMapping("/offers/{offerId}")
    public ResponseEntity<String> deleteOffer(@PathVariable String offerId) {
        int result = 0;
        try {
            result = interestService.deleteOffer(Integer.parseInt(offerId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting offer");
        }
        if (result == -1) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Offer not found");
        }

        return ResponseEntity.ok().body("Offer deleted successfully");
        
    }

    @PostMapping("/offerresponse")
    public ResponseEntity<String> updateOffer(@RequestBody OfferResponse offerRes) {
        int result = 0;
        try {
            result = interestService.updateOffer(offerRes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating offer");
        }
        if (result == -1) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Offer not found");
        }

        return ResponseEntity.ok().body("Offer updated successfully");
    }
}

package Tfast_Rmoney.Givvy.interfaces;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import Tfast_Rmoney.Givvy.interfaces.dtos.InterestDTO;
import Tfast_Rmoney.Givvy.interfaces.dtos.OfferDTO;
import Tfast_Rmoney.Givvy.interfaces.dtos.OfferResponse;
import Tfast_Rmoney.Givvy.security.AuctionUserDetails;
import Tfast_Rmoney.Givvy.security.WrongUserException;
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
    public ResponseEntity<String> expressInterest(Authentication authentication, @RequestBody InterestDTO interest) {
        AuctionUserDetails details = (AuctionUserDetails) authentication.getPrincipal();
        interest.setUserId(details.getUsername());
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
    public ResponseEntity<String> removeInterest(Authentication authentication, @PathVariable Integer interestId) {
        AuctionUserDetails details = (AuctionUserDetails) authentication.getPrincipal();
        int result = 0;

        try {
            result = interestService.deleteInterest(details.getUsername(), interestId);
        } catch (WrongUserException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User not authorized to delete this interest");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error removing interest");
        }

        if (result == -1) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Interest not found");
        }

        return ResponseEntity.ok().body("Interest removed successfully");
    }
    

    @GetMapping
    public ResponseEntity<List<InterestDTO>> findInterestsForUser(Authentication authentication) {
        AuctionUserDetails details = (AuctionUserDetails) authentication.getPrincipal();
        List<InterestDTO> results = interestService.getInterestsByRecipientId(details.getUsername());
        return ResponseEntity.ok().body(results);
    }

    @GetMapping(value ="/offers/recipient")
    public ResponseEntity<List<OfferDTO>> getOffersForRecipient(Authentication authentication) {
        AuctionUserDetails details = (AuctionUserDetails) authentication.getPrincipal();
        List<OfferDTO> offers = interestService.getOffersByRecipientId(details.getUsername());
        return ResponseEntity.ok().body(offers);
    }

    @GetMapping(value = "/offers/donor")
    public ResponseEntity<List<OfferDTO>> getOffersForDonor(Authentication authentication) {
        AuctionUserDetails details = (AuctionUserDetails) authentication.getPrincipal();
        List<OfferDTO> offers = interestService.getOffersByDonorId(details.getUsername());
        return ResponseEntity.ok().body(offers);
    }

    @PostMapping("/offers")
    public ResponseEntity<String> saveOffer(Authentication authentication, @RequestBody OfferDTO offer) {
        AuctionUserDetails details = (AuctionUserDetails) authentication.getPrincipal();
        offer.setDonorId(details.getUsername());
        int result = 0;

        try {
            result = interestService.saveOffer(offer);
        } catch (WrongUserException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User not authorized to make an offer on this interest");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving offer");
        }

        if (result == -1) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No associated interest found");
        }

        return ResponseEntity.ok().body("Offer saved successfully");
    }

    @DeleteMapping("/offers/{offerId}")
    public ResponseEntity<String> deleteOffer(Authentication authentication, @PathVariable Integer offerId) {
        AuctionUserDetails details = (AuctionUserDetails) authentication.getPrincipal();
        int result = 0;
        try {
            result = interestService.deleteOffer(details.getUsername(), offerId);
        } catch (WrongUserException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User not authorized to delete this offer");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting offer");
        }
        if (result == -1) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Offer not found");
        }

        return ResponseEntity.ok().body("Offer deleted successfully");
        
    }

    @PostMapping("/offerresponse")
    public ResponseEntity<String> updateOffer(Authentication authentication, @RequestBody OfferResponse offerRes) {
        AuctionUserDetails details = (AuctionUserDetails) authentication.getPrincipal();
        int result = 0;
        try {
            result = interestService.updateOffer(details.getUsername(), offerRes);
        } catch (WrongUserException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User not authorized to update this offer");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating offer");
        }
       
        if (result == -1) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Offer not found");
        }

        return ResponseEntity.ok().body("Offer updated successfully");
    }
}

package Tfast_Rmoney.Givvy.services;

import java.util.List;
import java.util.UUID;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import Tfast_Rmoney.Givvy.repositories.InterestRepository;
import Tfast_Rmoney.Givvy.repositories.OfferRepository;
import Tfast_Rmoney.Givvy.entities.Interest;
import Tfast_Rmoney.Givvy.entities.Offer;

@Service
public class InterestService {
    
@Autowired
private InterestRepository interestRepository;

@Autowired
private OfferRepository offerRepository;

@Autowired

public List<Interest> getInterestsByRecipientId(UUID recipientId) {
    return interestRepository.findByUserId(recipientId);
}

public List<Interest> getInterestsByItemId(String itemId) {
    return interestRepository.findByItemId(itemId);
}

public Interest expressInterest(Interest interest) {
    return interestRepository.save(interest);
}

public Integer deleteInterest(Integer interestId) {
    Optional<Interest> interestOpt = interestRepository.findById(interestId);
    if (!interestOpt.isPresent()) {
        return -1; // Or throw an exception if you prefer
    }
    Interest interest = interestOpt.get();

    Optional<Offer> offerOpt = offerRepository.findById(interest.getOffer().getOfferId());
    if (offerOpt.isPresent()) {
        Offer offer = offerOpt.get();

        if(offer.getStatus() == 1) {
            //Need to change status of item
            //Optional<Item> itemOpt = Optional.ofNullable(offer.getInterest().getItem());
        }
        //Need to change status of item
        //Optional<Item> itemOpt = Optional.ofNullable(offer.getInterest().getItem());
        offerRepository.deleteById(offer.getOfferId());
    }

    interestRepository.deleteById(interestId);

    return 1;
}




}

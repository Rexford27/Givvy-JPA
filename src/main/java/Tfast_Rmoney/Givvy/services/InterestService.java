package Tfast_Rmoney.Givvy.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import Tfast_Rmoney.Givvy.repositories.AppointmentRepository;
import Tfast_Rmoney.Givvy.repositories.AppointmentSchedulingRepository;
import Tfast_Rmoney.Givvy.repositories.InterestRepository;
import Tfast_Rmoney.Givvy.repositories.ItemRepository;
import Tfast_Rmoney.Givvy.repositories.OfferRepository;
import Tfast_Rmoney.Givvy.repositories.UserRepository;
import Tfast_Rmoney.Givvy.entities.Appointment;
import Tfast_Rmoney.Givvy.entities.Interest;
import Tfast_Rmoney.Givvy.entities.Item;
import Tfast_Rmoney.Givvy.entities.Offer;
import Tfast_Rmoney.Givvy.entities.User;
import Tfast_Rmoney.Givvy.interfaces.dtos.InterestDTO;
import Tfast_Rmoney.Givvy.interfaces.dtos.OfferDTO;
import Tfast_Rmoney.Givvy.interfaces.dtos.OfferResponse;
import Tfast_Rmoney.Givvy.security.WrongUserException;

@Service
public class InterestService {
    
@Autowired
private InterestRepository interestRepository;

@Autowired
private OfferRepository offerRepository;

@Autowired 
private UserRepository userRepository;

@Autowired 
private ItemRepository itemRepository;

@Autowired
private AppointmentRepository appointmentRepository;

@Autowired
private AppointmentSchedulingRepository appointmentSchedulingRepository;


public List<InterestDTO> getInterestsByRecipientId(String recipientId) {

    Optional<List<Interest>> interestsOpt = interestRepository.findByUserId(UUID.fromString(recipientId));
    List<InterestDTO> interestDTOs = new ArrayList<>();
    if(interestsOpt.isPresent()) {
        for(Interest interest : interestsOpt.get()) {
            InterestDTO dto = new InterestDTO(interest);
            interestDTOs.add(dto);
        }
    }
    return interestDTOs;
}

public List<InterestDTO> getInterestsByItemId(UUID itemId) {
    Optional<List<Interest>> interestsOpt = interestRepository.findByItemId(itemId);
    List<InterestDTO> interestDTOs = new ArrayList<>();

    if(interestsOpt.isPresent()) {
        for(Interest interest : interestsOpt.get()) {
            InterestDTO dto = new InterestDTO(interest);
            interestDTOs.add(dto);
        }
    }

    return interestDTOs;

}

public Integer expressInterest(InterestDTO interest) {
    Interest newInterest = new Interest();
    
    Optional<User> userOpt = userRepository.findById(UUID.fromString(interest.getUserId()));
    if (!userOpt.isPresent()) {
        return -1; // Or throw an exception if you prefer
    }
    newInterest.setUser(userOpt.get());
    Optional<Item> itemOpt = itemRepository.findById(UUID.fromString(interest.getItemId()));
    if (!itemOpt.isPresent()) {
        return -1; // Or throw an exception if you prefer
    }


    newInterest.setItem(itemOpt.get());
    newInterest.setExpressedAt(interest.getExpressedAt() != null ? LocalDateTime.parse(interest.getExpressedAt()) : null);

    interestRepository.save(newInterest);
    return 1;
}


public Integer deleteInterest(String userId, Integer interestId) throws WrongUserException {
    Optional<Interest> interestOpt = interestRepository.findById(interestId);
    if (!interestOpt.isPresent()) {
        return -1; // Or throw an exception if you prefer
    }

    if(!interestOpt.get().getUser().getUserId().toString().equals(userId)) {
        throw new WrongUserException(); 
    }


    Optional<Offer> offerOpt = offerRepository.getOfferByInterestId(interestId);
    if (offerOpt.isPresent()) {
        Offer offer = offerOpt.get();

            if(offer.getStatus() == 1) {
                //Need to change status of item
                Optional<Item> itemOpt = itemRepository.findById(offer.getInterest().getItem().getItemId());
                if (itemOpt.isPresent()) {
                    Item item = itemOpt.get();
                    item.setStatus("available"); // Assuming 'available' means "available"
                    itemRepository.save(item);
            }
            
        }
        offerRepository.deleteById(offer.getOfferId());
    }

    appointmentSchedulingRepository.deleteByInterestId(interestId);
    appointmentRepository.deleteByInterestId(interestId);


    interestRepository.deleteById(interestId);

    return 1;

}

// public Offer getAcceptedOffer(String itemId, UUID recipientId) {
//     return offerRepository.getAcceptedOffer(itemId, recipientId);
// }

public List<OfferDTO> getOffersByRecipientId(String recipientId) {
    Optional<List<Offer>> offersOpt = offerRepository.findByRecipientId(UUID.fromString(recipientId));
    List<OfferDTO> offerDTOs = new ArrayList<>();
    if(offersOpt.isPresent()) {
        for(Offer offer : offersOpt.get()) {
            OfferDTO dto = new OfferDTO(offer);
            offerDTOs.add(dto);
        }
    }
    return offerDTOs;
}

public List<OfferDTO> getOffersByDonorId(String donorId) {
    Optional<List<Offer>> offersOpt = offerRepository.findByDonorId(UUID.fromString(donorId.toString()));
    List<OfferDTO> offerDTOs = new ArrayList<>();
    if(offersOpt.isPresent()) {
        for(Offer offer : offersOpt.get()) {
            OfferDTO dto = new OfferDTO(offer);
            offerDTOs.add(dto);
        }
    }
    return offerDTOs;
}

public int saveOffer(OfferDTO offer) throws WrongUserException {
    Offer newOffer = new Offer(offer);

    Optional<Interest> interestOpt = interestRepository.findById(offer.getInterestId());
    if (!interestOpt.isPresent()) {
        return -1; // Or throw an exception if you prefer
    }
    if(!offer.getDonorId().toString().equals(interestOpt.get().getItem().getDonor().getUserId().toString())) {
        throw new WrongUserException(); // Or throw an exception if you prefer
    }
    newOffer.setInterest(interestOpt.get());

    Item item = interestOpt.get().getItem();
    item.setStatus("pending");
    itemRepository.save(item);

    offerRepository.save(newOffer);

    return 1;

}

public int deleteOffer(String donorId, Integer offerId) throws WrongUserException {

    Optional<Offer> offerOpt = offerRepository.findById(offerId);

    if (offerOpt.isPresent()) {
        Offer offer = offerOpt.get();
        if(!offer.getRecipientId().toString().equals(donorId)) {
            throw new WrongUserException(); // Or throw an exception if you prefer
        }

        if(offer.getStatus() == 1) {
            Optional<Item> itemOpt = itemRepository.findById(offer.getInterest().getItem().getItemId());
            if (itemOpt.isPresent()) {
                Item item = itemOpt.get();
                item.setStatus("available"); // Assuming 'available' means "available"
                itemRepository.save(item);
            }
        }

    appointmentRepository.deleteByInterestId(offer.getInterest().getId());
    appointmentSchedulingRepository.deleteByInterestId(offer.getInterest().getId());

    offerRepository.deleteById(offerId);

    return 1;
    }
    
    else {
        return -1; // Or throw an exception if you prefer   
    }

}

public int updateOffer(String recipientId, OfferResponse offerRes) throws WrongUserException {
    Optional<Offer> offerOpt = offerRepository.findById(offerRes.getOfferId());

    if (offerOpt.isPresent()) {
        Offer offer = offerOpt.get();
        if(!offer.getRecipientId().toString().equals(recipientId)) {
            throw new WrongUserException(); // Or throw an exception if you prefer
        }
        
        if(offerRes.isAccepted()) {
            Optional<Item> itemOpt = itemRepository.findById(offer.getInterest().getItem().getItemId());
            if (itemOpt.isPresent()) {
                Item item = itemOpt.get();
                item.setStatus("pending"); // Assuming 'pending' means "pending"
                itemRepository.save(item);
            }
            offer.setStatus(1); // Accepted = 1
        }
        else{
            offer.setStatus(2); // Rejected = 2
        }

        offerRepository.save(offer);
        return 1;
    }
    else {
        return -1; // Or throw an exception if you prefer   
    }
}




}

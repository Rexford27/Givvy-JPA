package Tfast_Rmoney.Givvy.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import Tfast_Rmoney.Givvy.entities.Offer;

public interface OfferRepository extends JpaRepository<Offer, Integer> {
    
    Optional<List<Offer>> findByRecipientId(UUID recipientId);
    Optional<List<Offer>> findByDonorId(UUID donorId);
    void deleteByInterestId(Integer interestId);
    //Make offer done by "save" method from JpaRepository
    //Delete offer done by "deleteById" method from JpaRepository

    //Might need to look deeper into limiting to 1 result for this query
    @Query("SELECT o FROM Offer o WHERE o.interest.item.itemId = :itemId AND o.recipientId = :recipientId AND o.status = 1")
    Optional<Offer> getAcceptedOffer(String itemId, UUID recipientId);

    @Query("SELECT o FROM Offer o WHERE o.interest.id = :interestId")
    Optional<Offer> getOfferByInterestId(Integer interestId);



    //Updating accepted offer happens in service
}
package Tfast_Rmoney.Givvy.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import Tfast_Rmoney.Givvy.entities.Offer;

public interface OfferRepository extends JpaRepository<Offer, Integer> {
    List<Offer> findByRecipientId(String recipientId);
    List<Offer> findByDonorId(String donorId);
    //Make offer done by "save" method from JpaRepository
    //Delete offer done by "deleteById" method from JpaRepository

    //Might need to look deeper into limiting to 1 result for this query
    @Query("SELECT o FROM Offer o WHERE o.interest.item.itemId = :itemId AND o.recipientId = :recipientId AND o.status = 1")
    Offer getAcceptedOffer(String itemId, String recipientId);

    //Updating accepted offer happens in service
}
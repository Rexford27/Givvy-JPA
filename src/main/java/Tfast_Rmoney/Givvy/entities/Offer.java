package Tfast_Rmoney.Givvy.entities;

import java.util.UUID;

import Tfast_Rmoney.Givvy.interfaces.dtos.OfferDTO;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

@Entity
public class Offer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer offer_id;
    
    @OneToOne
    private Interest interest;
    
    private UUID recipientId;
    
    private UUID donorId;
    
    private Integer status; // 0 = pending, 1 = accepted, 2 = rejected

    public Offer() {}

    public Offer(OfferDTO dto) {
        this.offer_id = dto.getOfferId();
        
        this.recipientId = dto.getRecipientId();
        this.donorId = dto.getDonorId();
        this.status = dto.getStatus();
    }

    // Getters
    public Integer getOfferId() { 
        return offer_id; 
    }
    
    public Interest getInterest() { 
        return interest; 
    }
    
    public UUID getRecipientId() { 
        return recipientId; 
    }
    
    public UUID getDonorId() { 
        return donorId; 
    }
    
    public Integer getStatus() { 
        return status; 
    }

    // Setters
    public void setOfferId(Integer offerId) { 
        this.offer_id = offerId; 
    }
    
    public void setInterest(Interest interest) { 
        this.interest = interest; 
    }
    
    public void setRecipientId(UUID recipientId) { 
        this.recipientId = recipientId; 
    }
    
    public void setDonorId(UUID donorId) { 
        this.donorId = donorId; 
    }
    
    public void setStatus(Integer status) { 
        this.status = status; 
    }
}
package Tfast_Rmoney.Givvy.entities;

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
    
    private String recipientId;
    
    private String donorId;
    
    private Integer status; // 0 = pending, 1 = accepted, 2 = rejected

    public Offer() {}

    // Getters
    public Integer getOfferId() { 
        return offer_id; 
    }
    
    public Interest getInterest() { 
        return interest; 
    }
    
    public String getRecipientId() { 
        return recipientId; 
    }
    
    public String getDonorId() { 
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
    
    public void setRecipientId(String recipientId) { 
        this.recipientId = recipientId; 
    }
    
    public void setDonorId(String donorId) { 
        this.donorId = donorId; 
    }
    
    public void setStatus(Integer status) { 
        this.status = status; 
    }
}
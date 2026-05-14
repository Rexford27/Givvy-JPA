package Tfast_Rmoney.Givvy.interfaces.dtos;

import Tfast_Rmoney.Givvy.entities.Offer;

import java.util.UUID;

import Tfast_Rmoney.Givvy.entities.Interest;

public class OfferDTO {
    private Integer offerId;
    private Integer interestId;
    private UUID recipientId;
    private UUID donorId;
    private Integer status;

    public OfferDTO() {}

    public OfferDTO(Offer core) {
        this.offerId = core.getOfferId();
        
        Interest interest = core.getInterest();
        if (interest != null) {
            this.interestId = interest.getId();
        }
        
        this.recipientId = core.getRecipientId();
        this.donorId = core.getDonorId();
        this.status = core.getStatus();
    }

    public Integer getOfferId() {
        return offerId;
    }

    public void setOfferId(Integer offerId) {
        this.offerId = offerId;
    }

    public Integer getInterestId() {
        return interestId;
    }

    public void setInterestId(Integer interestId) {
        this.interestId = interestId;
    }

    public UUID getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(UUID recipientId) {
        this.recipientId = recipientId;
    }

    public UUID getDonorId() {
        return donorId;
    }

    public void setDonorId(UUID donorId) {
        this.donorId = donorId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
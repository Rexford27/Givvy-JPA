package Tfast_Rmoney.Givvy.core;

public class Offer {
    private Integer offer_id;
    private Integer interestId;
    private String recipientId;
    private String donorId;
    private Integer status; // 0 = pending, 1 = accepted, 2 = rejected

    public Offer() {}

    public Offer(Integer offerId, Integer interestId, String recipientId, String donorId, Integer status) {
        this.offer_id = offerId;
        this.interestId = interestId;
        this.recipientId = recipientId;
        this.donorId = donorId;
        this.status = status;
    }

    // Getters
    public Integer getOfferId() { return offer_id; }
    public Integer getInterestId() { return interestId; }
    public String getRecipientId() { return recipientId; }
    public String getDonorId() { return donorId; }
    public Integer getStatus() { return status; }

    // Setters
    public void setOfferId(Integer offerId) { this.offer_id = offerId; }
    public void setInterestId(Integer interestId) { this.interestId = interestId; }
    public void setRecipientId(String recipientId) { this.recipientId = recipientId; }
    public void setDonorId(String donorId) { this.donorId = donorId; }
    public void setStatus(Integer status) { this.status = status; }
}
package Tfast_Rmoney.Givvy.interfaces.dtos;


public class OfferResponse {
    private String offerId;
    private boolean accepted;
    private String respondedAt;


    public OfferResponse() {}

    public OfferResponse(String offerId, boolean accepted, String respondedAt) {
        this.offerId = offerId;
        this.accepted = accepted;
        this.respondedAt = respondedAt;
    }   

    public String getOfferId() {
        return offerId;
    }

    public void setOfferId(String offerId) {
        this.offerId = offerId;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public String getRespondedAt() {
        return respondedAt;
    }

    public void setRespondedAt(String respondedAt) {
        this.respondedAt = respondedAt;
    }
}


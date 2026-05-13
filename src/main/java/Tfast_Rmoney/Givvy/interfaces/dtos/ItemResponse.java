package Tfast_Rmoney.Givvy.interfaces.dtos;

import java.time.LocalDateTime;

import Tfast_Rmoney.Givvy.entities.Item;

public class ItemResponse {

    private String itemId;
    private String donorId;
    private String title;
    private String description;
    private String imageUrl;
    private String status;
    private LocalDateTime postedAt;

    public ItemResponse(Item item) {
        this.itemId = item.getItemId().toString();

        if (item.getDonor() != null) {
            this.donorId = item.getDonor().getUserId().toString();
        }

        this.title = item.getTitle();
        this.description = item.getDescription();
        this.imageUrl = item.getImageUrl();
        this.status = item.getStatus();
        this.postedAt = item.getPostedAt();
    }

    public String getItemId() {
        return itemId;
    }

    public String getDonorId() {
        return donorId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getPostedAt() {
        return postedAt;
    }
}

package Tfast_Rmoney.Givvy.core;

import java.time.LocalDateTime;

public class Item {

    private String itemId;        // UUID
    private String donorId;       // userId (UUID)
    private String title;
    private String description;
    private String imageUrl;
    private String status;        // available, pending, scheduled, done
    private LocalDateTime postedAt;

    public Item() {}

    public Item(String itemId, String donorId, String title, String description,
                String imageUrl, String status, LocalDateTime postedAt) {
        this.itemId = itemId;
        this.donorId = donorId;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.status = status;
        this.postedAt = postedAt;
    }

    // Getters
    public String getItemId() { return itemId; }
    public String getDonorId() { return donorId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }
    public String getStatus() { return status; }
    public LocalDateTime getPostedAt() { return postedAt; }

    // Setters
    public void setItemId(String itemId) { this.itemId = itemId; }
    public void setDonorId(String donorId) { this.donorId = donorId; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setStatus(String status) { this.status = status; }
    public void setPostedAt(LocalDateTime postedAt) { this.postedAt = postedAt; }
}
package Tfast_Rmoney.Givvy.core;

import java.time.LocalDateTime;

public class Interest {
    private Integer id;
    private String userId;  // UUID of the user
    private String itemId;
    private LocalDateTime expressedAt;

    public Interest() {}

    public Interest(Integer id, String userId, String itemId, LocalDateTime expressedAt) {
        this.id = id;
        this.userId = userId;
        this.itemId = itemId;
        this.expressedAt = expressedAt;
    }

    // Getters
    public Integer getId() { return id; }
    public String getUserId() { return userId; }
    public String getItemId() { return itemId; }
    public LocalDateTime getExpressedAt() { return expressedAt; }

    // Setters
    public void setId(Integer id) { this.id = id; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setItemId(String itemId) { this.itemId = itemId; }
    public void setExpressedAt(LocalDateTime expressedAt) { this.expressedAt = expressedAt; }
}
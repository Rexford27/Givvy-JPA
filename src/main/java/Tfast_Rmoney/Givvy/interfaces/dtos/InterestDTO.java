package Tfast_Rmoney.Givvy.interfaces.dtos;


import Tfast_Rmoney.Givvy.entities.Interest;
import Tfast_Rmoney.Givvy.entities.Item;
import Tfast_Rmoney.Givvy.entities.User;

public class InterestDTO {
    private Integer id;
    private String userId;
    private String itemId;
    private String expressedAt;

    public InterestDTO() {}

    public InterestDTO(Interest core) {
        this.id = core.getId();
        
        User user = core.getUser();
        if (user != null) {
            this.userId = user.getUserId().toString();
        }
        
        Item item = core.getItem();
        if (item != null) {
            this.itemId = item.getItemId().toString();
        }
        
        this.expressedAt = core.getExpressedAt() != null ? core.getExpressedAt().toString() : null;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getExpressedAt() {
        return expressedAt;
    }

    public void setExpressedAt(String expressedAt) {
        this.expressedAt = expressedAt;
    }

}
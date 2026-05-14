package Tfast_Rmoney.Givvy.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import Tfast_Rmoney.Givvy.interfaces.dtos.InterestDTO;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

@Entity
public class Interest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne
    private User user;
    
    @ManyToOne
    private Item item;
    
    private LocalDateTime expressedAt;

    public Interest() {}

    public Interest(InterestDTO dto) {
        this.id = dto.getId();
        
        if (dto.getUserId() != null) {
            User user = new User();
            user.setUserId(UUID.fromString(dto.getUserId()));
            this.user = user;
        }
        
        if (dto.getItemId() != null) {
            Item item = new Item();
            item.setItemId(UUID.fromString(dto.getItemId()));
            this.item = item;
        }
        
        this.expressedAt = dto.getExpressedAt() != null ? LocalDateTime.parse(dto.getExpressedAt()) : null;
 
    }

    // Getters
    public Integer getId() { return id; }
    
    public User getUser() { 
        return user; 
    }
    
    public Item getItem() { 
        return item; 
    }
    
    public LocalDateTime getExpressedAt() { 
        return expressedAt; 
    }

    // Setters
    public void setId(Integer id) { this.id = id; }
    
    public void setUser(User user) { 
        this.user = user; 
    }
    
    public void setItem(Item item) { 
        this.item = item; 
    }
    
    
    public void setExpressedAt(LocalDateTime expressedAt) { 
        this.expressedAt = expressedAt; 
    }
}
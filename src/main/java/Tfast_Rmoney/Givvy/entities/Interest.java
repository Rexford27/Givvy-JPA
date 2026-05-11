package Tfast_Rmoney.Givvy.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import Tfast_Rmoney.Givvy.core.User;
import Tfast_Rmoney.Givvy.core.Item;

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
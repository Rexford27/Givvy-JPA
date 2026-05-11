package Tfast_Rmoney.Givvy.entities;

import java.time.LocalDate;
import java.time.LocalTime;

import Tfast_Rmoney.Givvy.core.TransferSite;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;

@Entity
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @OneToOne
    private Interest interest;
    
    @ManyToOne
    private TransferSite location;
    
    private LocalDate day;
    
    private LocalTime time;

    public Appointment() {}

    // Getters
    public Integer getId() { 
        return id; 
    }
    
    public Interest getInterest() { 
        return interest; 
    }
    
    public TransferSite getLocation() { 
        return location; 
    }
    
    public LocalDate getDay() { 
        return day; 
    }
    
    public LocalTime getTime() { 
        return time; 
    }

    // Setters
    public void setId(Integer id) { 
        this.id = id; 
    }
    
    public void setInterest(Interest interest) { 
        this.interest = interest; 
    }
    
    public void setLocation(TransferSite location) { 
        this.location = location; 
    }
    
    public void setDay(LocalDate day) { 
        this.day = day; 
    }
    
    public void setTime(LocalTime time) { 
        this.time = time; 
    }
}
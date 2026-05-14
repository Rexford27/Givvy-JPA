package Tfast_Rmoney.Givvy.entities;

import java.time.LocalDateTime;
import java.time.LocalTime;

import Tfast_Rmoney.Givvy.entities.TransferSite;
import Tfast_Rmoney.Givvy.interfaces.dtos.AppointmentSchedulingDTO;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class AppointmentScheduling {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne
    private Interest interest;
    
    @ManyToOne
    private TransferSite location;
    
    private LocalDateTime startTime;

    public AppointmentScheduling() {}

    public AppointmentScheduling(AppointmentSchedulingDTO dto) {
        
        this.startTime = dto.getStartTime() != null ? LocalDateTime.parse(dto.getStartTime()) : null;
    }

    // Getters
    public Integer getId() { return id; }
    
    public Interest getInterest() { 
        return interest; 
    }
    
    public TransferSite getLocation() { 
        return location; 
    }
    
    public LocalDateTime getStartTime() { 
        return startTime; 
    }

    // Setters
    public void setId(Integer id) { this.id = id; }
    
    public void setInterest(Interest interest) { 
        this.interest = interest; 
    }
    
    public void setLocation(TransferSite location) { 
        this.location = location; 
    }
    
    public void setStartTime(LocalDateTime startTime) { 
        this.startTime = startTime; 
    }
}
package Tfast_Rmoney.Givvy.entities;

import java.time.LocalDateTime;
import java.util.List;

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

    @OneToOne(mappedBy = "interest", cascade = CascadeType.REMOVE)
    private Offer offer;

    @OneToMany(mappedBy = "interest", cascade = CascadeType.REMOVE)
    private List<Appointment> appointments;

    @OneToMany(mappedBy = "interest", cascade = CascadeType.REMOVE)
    private List<AppointmentScheduling> appointmentSchedulings;
    
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
    
    public Offer getOffer() {
        return offer;
    }
    
    public List<Appointment> getAppointments() {
        return appointments;
    }
    
    public List<AppointmentScheduling> getAppointmentSchedulings() {
        return appointmentSchedulings;
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
    
    public void setOffer(Offer offer) {
        this.offer = offer;
    }
    
    public void setAppointments(List<Appointment> appointments) {
        this.appointments = appointments;
    }
    
    public void setAppointmentSchedulings(List<AppointmentScheduling> appointmentSchedulings) {
        this.appointmentSchedulings = appointmentSchedulings;
    }
    
    public void setExpressedAt(LocalDateTime expressedAt) { 
        this.expressedAt = expressedAt; 
    }
}
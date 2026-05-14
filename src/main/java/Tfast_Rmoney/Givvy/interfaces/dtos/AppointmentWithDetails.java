package Tfast_Rmoney.Givvy.interfaces.dtos;

import java.time.LocalDate;
import java.time.LocalTime;

import Tfast_Rmoney.Givvy.entities.Appointment;

public class AppointmentWithDetails {
    private Integer id;
    private Integer interestId;
    private Integer locationId;
    private LocalDate day;
    private LocalTime time;

    // Item info (via JOIN from interest)
    private String itemId;
    private String itemTitle;
    private String donorId;
    private String status;

    // Recipient info (via JOIN from interest)
    private String userId;
    private String userName;
    private String userEmail;

    public AppointmentWithDetails() {}

    public AppointmentWithDetails(Appointment appointment) {
        this.id = appointment.getId();
        this.interestId = appointment.getInterest() != null ? appointment.getInterest().getId() : null;
        this.locationId = appointment.getLocation() != null ? appointment.getLocation().getId() : null;
        this.day = appointment.getDay();
        this.time = appointment.getTime();

        if (appointment.getInterest() != null) {
            if (appointment.getInterest().getItem() != null) {
                this.itemId = appointment.getInterest().getItem().getItemId().toString();
                this.itemTitle = appointment.getInterest().getItem().getTitle();
                this.donorId = appointment.getInterest().getItem().getDonor() != null 
                    ? appointment.getInterest().getItem().getDonor().getUserId().toString() 
                    : null;
                this.status = appointment.getInterest().getItem().getStatus();
            }
            
            if (appointment.getInterest().getUser() != null) {
                this.userId = appointment.getInterest().getUser().getUserId().toString();
                this.userName = appointment.getInterest().getUser().getName();
                this.userEmail = appointment.getInterest().getUser().getEmail();
            }
        }
    }
    // Getters
    public Integer getId() { return id; }
    public Integer getInterestId() { return interestId; }
    public Integer getLocationId() { return locationId; }
    public LocalDate getDay() { return day; }
    public LocalTime getTime() { return time; }
    public String getItemId() { return itemId; }
    public String getItemTitle() { return itemTitle; }
    public String getDonorId() { return donorId; }
    public String getStatus() { return status; }
    public String getUserId() { return userId; }
    public String getUserName() { return userName; }
    public String getUserEmail() { return userEmail; }

    // Setters
    public void setId(Integer id) { this.id = id; }
    public void setInterestId(Integer interestId) { this.interestId = interestId; }
    public void setLocationId(Integer locationId) { this.locationId = locationId; }
    public void setDay(LocalDate day) { this.day = day; }
    public void setTime(LocalTime time) { this.time = time; }
    public void setItemId(String itemId) { this.itemId = itemId; }
    public void setItemTitle(String itemTitle) { this.itemTitle = itemTitle; }
    public void setDonorId(String donorId) { this.donorId = donorId; }
    public void setStatus(String status) { this.status = status; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setUserName(String userName) { this.userName = userName; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
}
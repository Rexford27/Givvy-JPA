package Tfast_Rmoney.Givvy.core;

import java.time.LocalDate;
import java.time.LocalTime;

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
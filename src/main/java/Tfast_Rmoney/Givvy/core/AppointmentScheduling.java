package Tfast_Rmoney.Givvy.core;

import java.time.LocalDateTime;

public class AppointmentScheduling {
    private Integer id;
    private Integer interestId;  // Link to interest table - item and recipient come from here
    private Integer locationId;
    private LocalDateTime startTime;

    public AppointmentScheduling() {}

    public AppointmentScheduling(Integer id, Integer interestId, Integer locationId,
                                 LocalDateTime startTime) {
        this.id = id;
        this.interestId = interestId;
        this.locationId = locationId;
        this.startTime = startTime;
    }

    // Getters
    public Integer getId() { return id; }
    public Integer getInterestId() { return interestId; }
    public Integer getLocationId() { return locationId; }
    public LocalDateTime getStartTime() { return startTime; }

    // Setters
    public void setId(Integer id) { this.id = id; }
    public void setInterestId(Integer interestId) { this.interestId = interestId; }
    public void setLocationId(Integer locationId) { this.locationId = locationId; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
}
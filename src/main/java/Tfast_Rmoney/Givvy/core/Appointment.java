package Tfast_Rmoney.Givvy.core;

import java.time.LocalDate;
import java.time.LocalTime;

public class Appointment {
    private Integer id;
    private Integer interestId;  // Link to interest table - item and recipient come from here
    private Integer locationId;
    private LocalDate day;
    private LocalTime time;

    public Appointment() {}

    public Appointment(Integer id, Integer interestId, Integer locationId,
                       LocalDate day, LocalTime time) {
        this.id = id;
        this.interestId = interestId;
        this.locationId = locationId;
        this.day = day;
        this.time = time;
    }

    // Getters
    public Integer getId() { return id; }
    public Integer getInterestId() { return interestId; }
    public Integer getLocationId() { return locationId; }
    public LocalDate getDay() { return day; }
    public LocalTime getTime() { return time; }

    // Setters
    public void setId(Integer id) { this.id = id; }
    public void setInterestId(Integer interestId) { this.interestId = interestId; }
    public void setLocationId(Integer locationId) { this.locationId = locationId; }
    public void setDay(LocalDate day) { this.day = day; }
    public void setTime(LocalTime time) { this.time = time; }
}
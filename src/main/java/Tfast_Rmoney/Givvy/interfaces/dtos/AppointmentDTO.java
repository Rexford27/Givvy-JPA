package Tfast_Rmoney.Givvy.interfaces.dtos;

import java.time.LocalDate;
import java.time.LocalTime;

import Tfast_Rmoney.Givvy.entities.Appointment;
import Tfast_Rmoney.Givvy.entities.Interest;
import Tfast_Rmoney.Givvy.entities.TransferSite;

public class AppointmentDTO {
    private Integer id;
    private Integer interestId;
    private Integer locationId;
    private String day;
    private String time;

    public AppointmentDTO() {}

    public AppointmentDTO(Appointment core) {
        this.id = core.getId();
        
        Interest interest = core.getInterest();
        if (interest != null) {
            this.interestId = interest.getId();
        }
        
        TransferSite location = core.getLocation();
        if (location != null) {
            this.locationId = location.getId();
        }
        
        this.day = core.getDay() != null ? core.getDay().toString() : null;
        this.time = core.getTime() != null ? core.getTime().toString() : null;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getInterestId() {
        return interestId;
    }

    public void setInterestId(Integer interestId) {
        this.interestId = interestId;
    }

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
package Tfast_Rmoney.Givvy.interfaces.dtos;

import java.time.LocalDateTime;

import Tfast_Rmoney.Givvy.entities.AppointmentScheduling;
import Tfast_Rmoney.Givvy.entities.Interest;
import Tfast_Rmoney.Givvy.core.TransferSite;

public class AppointmentSchedulingDTO {
    private Integer id;
    private Integer interestId;
    private Integer locationId;
    private String startTime;

    public AppointmentSchedulingDTO() {}

    public AppointmentSchedulingDTO(AppointmentScheduling core) {
        this.id = core.getId();
        
        Interest interest = core.getInterest();
        if (interest != null) {
            this.interestId = interest.getId();
        }
        
        TransferSite location = core.getLocation();
        if (location != null) {
            this.locationId = location.getId();
        }
        
        this.startTime = core.getStartTime() != null ? core.getStartTime().toString() : null;
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

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
}
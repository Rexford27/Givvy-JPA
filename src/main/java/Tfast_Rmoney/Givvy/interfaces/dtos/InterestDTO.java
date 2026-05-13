package Tfast_Rmoney.Givvy.interfaces.dtos;

import java.util.ArrayList;
import java.util.List;

import Tfast_Rmoney.Givvy.entities.Interest;
import Tfast_Rmoney.Givvy.entities.Item;
import Tfast_Rmoney.Givvy.entities.Appointment;
import Tfast_Rmoney.Givvy.entities.AppointmentScheduling;
import Tfast_Rmoney.Givvy.entities.Offer;
import Tfast_Rmoney.Givvy.entities.User;

public class InterestDTO {
    private Integer id;
    private String userId;
    private String itemId;
    private String expressedAt;
    private Integer offerId;
    private List<Integer> appointmentIds = new ArrayList<>();
    private List<Integer> appointmentSchedulingIds = new ArrayList<>();

    public InterestDTO() {}

    public InterestDTO(Interest core) {
        this.id = core.getId();
        
        User user = core.getUser();
        if (user != null) {
            this.userId = user.getUserId();
        }
        
        Item item = core.getItem();
        if (item != null) {
            this.itemId = item.getItemId();
        }
        
        this.expressedAt = core.getExpressedAt() != null ? core.getExpressedAt().toString() : null;
        
        Offer offer = core.getOffer();
        if (offer != null) {
            this.offerId = offer.getOfferId();
        }
        
        List<Appointment> appointments = core.getAppointments();
        if (appointments != null) {
            for (Appointment appt : appointments) {
                this.appointmentIds.add(appt.getId());
            }
        }
        
        List<AppointmentScheduling> schedulings = core.getAppointmentSchedulings();
        if (schedulings != null) {
            for (AppointmentScheduling sched : schedulings) {
                this.appointmentSchedulingIds.add(sched.getId());
            }
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getExpressedAt() {
        return expressedAt;
    }

    public void setExpressedAt(String expressedAt) {
        this.expressedAt = expressedAt;
    }

    public Integer getOfferId() {
        return offerId;
    }

    public void setOfferId(Integer offerId) {
        this.offerId = offerId;
    }

    public List<Integer> getAppointmentsIds() {
        return appointmentIds;
    }

    public void setAppointmentsIds(List<Integer> appointmentsIds) {
        this.appointmentIds = appointmentsIds;
    }

    public List<Integer> getAppointmnetSchedulingIds() {
        return appointmentSchedulingIds;
    }

    public void setAppointmentSchedulingIds(List<Integer> appointmentSchedulingIds) {
        this.appointmentSchedulingIds = appointmentSchedulingIds;
    }
}
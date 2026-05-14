package Tfast_Rmoney.Givvy.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import Tfast_Rmoney.Givvy.entities.Appointment;
import Tfast_Rmoney.Givvy.entities.AppointmentScheduling;
import Tfast_Rmoney.Givvy.entities.Interest;
import Tfast_Rmoney.Givvy.entities.Item;
import Tfast_Rmoney.Givvy.entities.TransferSite;
import Tfast_Rmoney.Givvy.interfaces.dtos.AppointmentDTO;
import Tfast_Rmoney.Givvy.interfaces.dtos.AppointmentSchedulingDTO;
import Tfast_Rmoney.Givvy.interfaces.dtos.AppointmentWithDetails;
import Tfast_Rmoney.Givvy.repositories.AppointmentRepository;
import Tfast_Rmoney.Givvy.repositories.AppointmentSchedulingRepository;
import Tfast_Rmoney.Givvy.repositories.InterestRepository;
import Tfast_Rmoney.Givvy.repositories.ItemRepository;
import Tfast_Rmoney.Givvy.repositories.OfferRepository;
import Tfast_Rmoney.Givvy.repositories.TransferSiteRepository;


@Service
public class AppointmentSchedulingService {

    @Autowired
    AppointmentRepository appointmentRepository;

    @Autowired
    TransferSiteRepository transferSiteRepository;

    @Autowired
    InterestRepository interestRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    OfferRepository offerRepository;

    @Autowired
    AppointmentSchedulingRepository appointmentSchedulingRepository;


    public int proposeAppointment(AppointmentSchedulingDTO appointmentSchedulingDTO) {
        Optional<Interest> interestOpt = interestRepository.findById(appointmentSchedulingDTO.getInterestId());
        Optional<TransferSite> locationOpt = transferSiteRepository.findById(appointmentSchedulingDTO.getLocationId());

        if (!interestOpt.isPresent() || !locationOpt.isPresent()) {
            return -1; // Or throw an exception if you prefer
        }

        // Create and save the appointment
        AppointmentScheduling appt = new AppointmentScheduling();
        appt.setStartTime(appointmentSchedulingDTO.getStartTime() != null ? LocalDateTime.parse(appointmentSchedulingDTO.getStartTime()) : null);
        appt.setLocation(locationOpt.get());
        appt.setInterest(interestOpt.get());

        appointmentSchedulingRepository.save(appt);

        return 1;
    }

    public List<AppointmentSchedulingDTO> getApptSchedulesForUser(UUID userId) {

        List<AppointmentScheduling> appointments = appointmentSchedulingRepository.getApptSchedulesForUsers(userId);
        List<AppointmentSchedulingDTO> appointmentDTOs = new ArrayList<>();

        for (AppointmentScheduling appt : appointments) {
            appointmentDTOs.add(new AppointmentSchedulingDTO(appt));
        }

        return appointmentDTOs;
    }


    public List<AppointmentSchedulingDTO> getApptSchedulesByItem(UUID itemId) {
        List<AppointmentScheduling> appointments = appointmentSchedulingRepository.getApptSchedulesByItem(itemId);
        List<AppointmentSchedulingDTO> appointmentDTOs = new ArrayList<>();

        for (AppointmentScheduling appt : appointments) {
            appointmentDTOs.add(new AppointmentSchedulingDTO(appt));
        }

        return appointmentDTOs;
    }


    public void removeAppointmentScheduleById(Integer appointmentSchedulingId) {
            appointmentSchedulingRepository.deleteById(appointmentSchedulingId);
    }

    public void removeAppointmentScheduleByItemId(UUID itemId) {
        List<AppointmentScheduling> appointments = appointmentSchedulingRepository.getApptSchedulesByItem(itemId);
        for (AppointmentScheduling appt : appointments) {
            appointmentSchedulingRepository.deleteById(appt.getId());
        }
    }
}
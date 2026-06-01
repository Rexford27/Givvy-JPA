package Tfast_Rmoney.Givvy.services;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;

import Tfast_Rmoney.Givvy.entities.Appointment;
import Tfast_Rmoney.Givvy.entities.Interest;
import Tfast_Rmoney.Givvy.entities.Item;
import Tfast_Rmoney.Givvy.entities.TransferSite;
import Tfast_Rmoney.Givvy.interfaces.dtos.AppointmentDTO;
import Tfast_Rmoney.Givvy.interfaces.dtos.AppointmentWithDetails;
import Tfast_Rmoney.Givvy.repositories.AppointmentRepository;
import Tfast_Rmoney.Givvy.repositories.AppointmentSchedulingRepository;
import Tfast_Rmoney.Givvy.repositories.InterestRepository;
import Tfast_Rmoney.Givvy.repositories.ItemRepository;
import Tfast_Rmoney.Givvy.repositories.OfferRepository;
import Tfast_Rmoney.Givvy.repositories.TransferSiteRepository;
import Tfast_Rmoney.Givvy.security.WrongUserException;
import jakarta.transaction.Transactional;


@Service
public class AppointmentService {

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


    private static final List<LocalTime> BASE_TIME_SLOTS = generateBaseTimes();

    private static List<LocalTime> generateBaseTimes() {
        List<LocalTime> times = new ArrayList<>();
        LocalTime startTime = LocalTime.of(7, 0);
        LocalTime endTime = LocalTime.of(22, 0);
        LocalTime currentTime = startTime;

        while (!currentTime.isAfter(endTime)) {
            times.add(currentTime);
            currentTime = currentTime.plusMinutes(15);
        }
        return times;
    }


    public List<LocalTime> findAvailableTimes(LocalDate day, Integer locationId) {
       List<LocalTime> availableTimes = new ArrayList<>(BASE_TIME_SLOTS);

        List<Appointment> appointments = appointmentRepository.getApptByDateAndLocation(day, locationId);

        List<LocalTime> bookedTimes = new ArrayList<>();
        for (Appointment a : appointments) {
            bookedTimes.add(a.getTime());
        }

        availableTimes.removeIf(bookedTimes::contains);
        return availableTimes;
    }

    public int saveAppointment(String userId, AppointmentDTO appointment) throws WrongUserException {
        Appointment apptEntity = new Appointment();

        apptEntity.setDay(appointment.getDay() != null ? LocalDate.parse(appointment.getDay()) : null);
        apptEntity.setTime(appointment.getTime() != null ? LocalTime.parse(appointment.getTime()) : null);

        Optional<TransferSite> locationOpt = transferSiteRepository.findById(appointment.getLocationId());
        if(locationOpt.isPresent()) {
            apptEntity.setLocation(locationOpt.get());
        } else {
            // Handle missing location
            return -1;
        }

        Optional<Interest> interestOpt = interestRepository.findById(appointment.getInterestId());
        if(interestOpt.isPresent()) {
            apptEntity.setInterest(interestOpt.get());
        } else {
            // Handle missing interest
            return -1;
        }

        Item item = interestOpt.get().getItem();
        if(!item.getDonor().getUserId().toString().equals(userId)) {
            throw new WrongUserException();
        }

        item.setStatus("scheduled"); // Assuming 'pending' means "pending"
        itemRepository.save(item);

        appointmentRepository.save(apptEntity);
        return 1;
    }

    public AppointmentDTO getAppointmentById(String userId, Integer id) throws WrongUserException {
        Optional<Appointment> apptOpt = appointmentRepository.findById(id);

        if (apptOpt.isPresent()) {
            Appointment appt = apptOpt.get();
            if(!appt.getInterest().getItem().getDonor().getUserId().toString().equals(userId) && !appt.getInterest().getUser().getUserId().toString().equals(userId)) {
                throw new WrongUserException();
            }
            return new AppointmentDTO(appt);
        }
        return null;
    }

    public AppointmentWithDetails getAppointmentDetails(String userId,Integer id) throws WrongUserException {
        Optional<Appointment> apptOpt = appointmentRepository.findById(id);
        
        if (apptOpt.isPresent()) {
            Appointment appt = apptOpt.get();
            if(!appt.getInterest().getItem().getDonor().getUserId().toString().equals(userId) && !appt.getInterest().getUser().getUserId().toString().equals(userId)) {
                throw new WrongUserException();
            }

            AppointmentWithDetails apptDets = new AppointmentWithDetails(appt);
            return apptDets;
        }
        return null;
    }

    public int cancelAppointment(String userId, Integer id) throws WrongUserException {
        Optional<Appointment> apptOpt = appointmentRepository.findById(id);
        if (!apptOpt.isPresent()) {
            return -1; // Or throw an exception if you prefer
        }
        Appointment appt = apptOpt.get();
        UUID itemId = appt.getInterest().getItem().getItemId();

        if(!appt.getInterest().getItem().getDonor().getUserId().toString().equals(userId) && !appt.getInterest().getUser().getUserId().toString().equals(userId)) {
            throw new WrongUserException();
        }
        
        Optional<Item> item = itemRepository.findById(itemId);
            if (item.isPresent()) {
                Item itemEntity = item.get();
                itemEntity.setStatus("pending");
                itemRepository.save(itemEntity);
            }

        appointmentRepository.deleteById(id);
        return 1;

        }

    public List<AppointmentDTO> getAppointmentsForUser(String userId) {
        List<Appointment> appointments = appointmentRepository.getApptsForUsers(UUID.fromString(userId));
        List<AppointmentDTO> appointmentDTOs = new ArrayList<>();

        for (Appointment appt : appointments) {
            appointmentDTOs.add(new AppointmentDTO(appt));
        }

        return appointmentDTOs;
        }

        @Modifying
        @Transactional
        public int completeAndDeleteAppointment(String userId, Integer id) throws WrongUserException {
            
            Optional<Appointment> apptOpt = appointmentRepository.findById(id);
            if (!apptOpt.isPresent()) {
                return -1; // Or throw an exception if you prefer
            }
            Appointment appt = apptOpt.get();
            UUID itemId = appt.getInterest().getItem().getItemId();
            Integer interestId = appt.getInterest().getId();

            if(!appt.getInterest().getItem().getDonor().getUserId().toString().equals(userId) && !appt.getInterest().getUser().getUserId().toString().equals(userId)) {
                throw new WrongUserException();
            }

            appointmentSchedulingRepository.deleteByInterestId(interestId);

            appointmentRepository.deleteById(id);

            offerRepository.deleteByInterestId(interestId);

            interestRepository.deleteById(interestId);

            itemRepository.deleteById(itemId);
                
            return 1;
        }





    }
    


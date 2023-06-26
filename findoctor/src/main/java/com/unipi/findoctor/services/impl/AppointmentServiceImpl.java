package com.unipi.findoctor.services.impl;

import com.unipi.findoctor.dto.AppointmentDetailsDto;
import com.unipi.findoctor.dto.AppointmentDto;
import com.unipi.findoctor.mappers.AppointmentMapper;
import com.unipi.findoctor.models.Appointment;
import com.unipi.findoctor.repositories.AppointmentRepository;
import com.unipi.findoctor.services.AppointmentService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@AllArgsConstructor
@Service
public class AppointmentServiceImpl implements AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final AppointmentMapper appointmentMapper;

    @Override
    public Map<String, Boolean> getDoctorAvailableTimeSlots(String doctorUsername, LocalDate date) {

        Map<String, Boolean> time_slots = new LinkedHashMap<>();

        List<Appointment> appointments = appointmentRepository.findAppointmentsByDoctor_User_UsernameAndDate(doctorUsername, date);

        List<LocalTime> appointment_times = appointments.stream()
                .map(Appointment::getTimeSlot)
                .toList();

        // Define the start and end time for available slots
        LocalTime startTime = LocalTime.of(8, 0); // 8:00
        LocalTime endTime = LocalTime.of(20, 0); // 20:00

        // Iterate through the time slots from start to end with one hour interval
        LocalTime currentSlot = startTime;
        while (!currentSlot.isAfter(endTime)) {
            // Check if the slot is not in the appointments list
            if (appointment_times.contains(currentSlot)) {
                time_slots.put(currentSlot.toString(), false);
            } else {
                time_slots.put(currentSlot.toString(), true);
            }

            // Move to the next slot
            currentSlot = currentSlot.plusHours(1);
        }


        return time_slots;
    }

    @Override
    public AppointmentDto saveAppointment(AppointmentDto appointmentDto) {
        Appointment savedAppointment = appointmentRepository.save(appointmentMapper.mapToAppointment(appointmentDto));
        return appointmentMapper.mapToAppointmentDto(savedAppointment);
    }

    @Override
    public List<AppointmentDetailsDto> fetchDoctorAppointments(String doctorUsername, LocalDate date) {

        List<Appointment> appointments = appointmentRepository.findAppointmentsByDoctor_User_UsernameAndDate(doctorUsername, date);

        if (appointments.isEmpty()) {
            return null;
        }

        List<AppointmentDetailsDto> appointmentDetailDtos = appointments.stream()
                .map(appointment -> appointmentMapper.mapToAppointmentDetailsDto(appointment))
                .toList();

        return appointmentDetailDtos;
    }

    @Override
    public void deleteById(Long id, String doctorUsername) throws RuntimeException {
        if (!appointmentRepository.existsAppointmentByIdAndDoctor_User_Username(id, doctorUsername)) {
            throw new RuntimeException("Can't delete");
        }

        appointmentRepository.deleteById(id);
    }
}

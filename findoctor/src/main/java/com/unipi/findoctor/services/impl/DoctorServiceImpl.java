package com.unipi.findoctor.services.impl;

import com.unipi.findoctor.dto.DoctorDto;
import com.unipi.findoctor.mappers.DoctorMapper;
import com.unipi.findoctor.models.Doctor;
import com.unipi.findoctor.repositories.DoctorRepository;
import com.unipi.findoctor.security.SecurityConfig;
import com.unipi.findoctor.services.DoctorService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static com.unipi.findoctor.constants.ControllerConstants.USER_TYPE_DOCTOR;


@AllArgsConstructor
@Service
public class DoctorServiceImpl implements DoctorService {
    private final DoctorRepository doctorRepository;
    private final DoctorMapper doctorMapper;

    @Override
    public DoctorDto getDoctorDetailsByUsername(String username) {
        Doctor doctor = doctorRepository.findByUser_username(username);

        if (doctor == null) {
            return null;
        }

        if (!doctor.getStatus().equals("approved")) {
            return null;
        }

        return doctorMapper.mapToDoctorDto(doctor);
    }

    @Override
    public Doctor findDoctor(String username) {
        return doctorRepository.findByUser_username(username);
    }

    @Override
    public boolean doctorExists(String username) {
        Optional<Doctor> doctor = Optional.ofNullable(doctorRepository.findByUser_username(username));
        return doctor.isPresent();
    }

    @Override
    public Page<DoctorDto> getDoctorsByPage(String query, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Doctor> doctorsPage;

        if (query == null) {
            doctorsPage = doctorRepository.findAll(pageable);
        } else {
            doctorsPage = doctorRepository.searchDoctors(query, pageable);
        }

        return doctorsPage.map(doctorMapper::mapToDoctorDto);
    }

    @Override
    public void updateDoctor(Doctor doctor) {
        Doctor existingDoctor = doctorRepository.findByUser_username(doctor.getUser().getUsername());

        if (existingDoctor == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        existingDoctor.getUser().setName(doctor.getUser().getName());
        existingDoctor.getUser().setSurname(doctor.getUser().getSurname());
        existingDoctor.getUser().setPhone(doctor.getUser().getPhone());
        existingDoctor.setBusinessPhone(doctor.getBusinessPhone());
        existingDoctor.getUser().setEmail(doctor.getUser().getEmail());
        existingDoctor.setCity(doctor.getCity());
        existingDoctor.setAddress(doctor.getAddress());

        doctorRepository.save(existingDoctor);
    }


    @Override
    public void saveDoctor(Doctor doctor) {
        doctor.getUser().setPassword(SecurityConfig.passwordEncoder().encode(doctor.getUser().getPassword()));
        doctorRepository.save(doctor);
    }

    @Override
    public void updateDoctorStatus(Doctor doctor) {
        doctorRepository.save(doctor);
    }

    @Override
    public Doctor findByAfm(String afm) {
        return doctorRepository.findByAfm(afm);
    }


}

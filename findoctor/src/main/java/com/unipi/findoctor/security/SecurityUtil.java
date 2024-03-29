package com.unipi.findoctor.security;

import com.unipi.findoctor.dto.AuthDto;
import com.unipi.findoctor.dto.PatientDto;
import com.unipi.findoctor.services.PatientService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import static com.unipi.findoctor.constants.ControllerConstants.*;

@Service
@AllArgsConstructor
public class SecurityUtil {
    private PatientService patientService;

    public AuthDto getSessionUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            return new AuthDto(
                    authentication.getName(),
                    authentication.getAuthorities().toArray()[0].toString());
        }
        return null;
    }

    public PatientDto getSessionPatient() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }

        String userType = authentication.getAuthorities().toArray()[0].toString();
        if (!userType.equals(USER_TYPE_PATIENT)) {
            return null;
        }

        return patientService.findPatient(authentication.getName());
    }

    public boolean isPatientLoggedIn() {
        PatientDto patientDto = getSessionPatient();

        return patientDto != null;
    }

    public String redirectBasedOnUserRole(String role) {
        // Redirect user based on his role
        switch (role) {
            case USER_TYPE_ADMIN -> {
                return "redirect:" + ADMIN_ROOT_URL;
            }
            case USER_TYPE_DOCTOR -> {
                return "redirect:" + DOCTOR_ROOT_URL;
            }
            default -> {  // patient
                return "redirect:" + PATIENT_ROOT_URL;
            }
        }
    }
}

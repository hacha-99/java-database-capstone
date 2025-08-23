package com.project.back_end.services;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Admin;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AdminRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;

@org.springframework.stereotype.Service
public class Service {

    private final TokenService tokenService;
    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final DoctorService doctorService;
    private final PatientService patientService;

    private static final Logger logger = LoggerFactory.getLogger(Service.class);

    public Service(TokenService tokenService, AdminRepository adminRepository, DoctorRepository doctorRepository,
            PatientRepository patientRepository, DoctorService doctorService, PatientService patientService) {
        this.tokenService = tokenService;
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.doctorService = doctorService;
        this.patientService = patientService;
    }

    // calls tokenservice to validate token
    public ResponseEntity<Map<String, String>> validateToken(String token, String user) {
        try {
            boolean tokenValid = tokenService.validateToken(token, user);
            if (!tokenValid) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Token invalid."));
            }
            return ResponseEntity.ok(Map.of("message", "Token valid."));
        } catch (Exception e) {
            logger.error("Exception in validateToken", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to validate token."));
        }
    }

    // checks admin login credentials
    public ResponseEntity<Map<String, String>> validateAdmin(Admin receivedAdmin) {
        try {
            Admin admin = adminRepository.findByUsername(receivedAdmin.getUsername());
            if (admin == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "No matching credentials."));
            }
            if (!admin.getPassword().equals(receivedAdmin.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "No matching credentials."));
            }
            return ResponseEntity.ok(Map.of("token", tokenService.generateToken(receivedAdmin.getUsername())));
        } catch (Exception e) {
            logger.error("Exception in validateAdmin", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to validate admin."));
        }
    }

    // wrap all filters in one method and check whether values are provided
    public Map<String, Object> filterDoctor(String name, String specialty, String time) {
        try {
            boolean hasName = name != null && !name.isBlank();
            boolean hasSpecialty = specialty != null && !specialty.isBlank();
            boolean hasTime = time != null && !time.isBlank();

            if (hasName && hasSpecialty && hasTime) {
                return doctorService.filterDoctorsByNameSpecialtyAndTime(name, specialty, time);
            } else if (hasName && hasSpecialty) {
                return doctorService.filterDoctorByNameAndSpecialty(name, specialty);
            } else if (hasName && hasTime) {
                return doctorService.filterDoctorByNameAndTime(name, time);
            } else if (hasSpecialty && hasTime) {
                return doctorService.filterDoctorByTimeAndSpecialty(time, specialty);
            } else if (hasName) {
                return doctorService.findDoctorByName(name);
            } else if (hasSpecialty) {
                return doctorService.filterDoctorBySpecialty(specialty);
            } else if (hasTime) {
                return doctorService.filterDoctorsByTime(time);
            } else {
                return Map.of("doctors", doctorRepository.findAll());
            }
        } catch (Exception e) {
            logger.error("Exception in filterDoctors", e);
            return Map.of("doctors", Collections.<Doctor>emptyList());
        }
    }

    // check whether appointment is available
    public int validateAppointment(Appointment appointment) {
        try {
            Doctor doctor = doctorRepository.findById(appointment.getDoctor().getId()).orElseThrow();

            // checks if doctor generally offers time slot declared in appointment object
            // parse time slots to LocalTime and compare with appointment LocalTime; if
            // there any match -> true
            boolean hasSlot = doctor.getAvailableTimes().stream()
                    .map(slot -> LocalTime.parse(slot.substring(0, 5),
                            DateTimeFormatter.ofPattern("HH:mm")))
                    .anyMatch(time -> time.equals(appointment.getAppointmentTimeOnly()));

            if (!hasSlot)
                return 0;

            boolean slotAvailable = doctorService
                    .getDoctorAvailability(appointment.getDoctor().getId(),
                            appointment.getAppointmentTime().toLocalDate())
                    .stream().map(slot -> LocalTime.parse(slot.substring(0, 5),
                            DateTimeFormatter.ofPattern("HH:mm")))
                    .anyMatch(time -> time.equals(appointment.getAppointmentTimeOnly()));

            if (hasSlot && slotAvailable) {
                return 1;
            }
            return 0;
        } catch (Exception e) {
            logger.error("Exception in validateAppointment.", e);
            return -1;
        }
    }

    // checks if patient already exists
    public boolean validatePatient(Patient patient) {
        try {
            Patient existingPatient = patientRepository.findByEmailOrPhone(patient.getEmail(), patient.getPhone());
            if (existingPatient == null)
                return true;
            return false;
        } catch (Exception e) {
            logger.error("Exception in validatePatient", e);
            return false;
        }
    }

    @Transactional
    public ResponseEntity<Map<String, String>> validatePatientLogin(Login login) {
        try {
            Patient patient = patientRepository.findByEmail(login.getEmail());
            if (patient == null)
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        (Map.of("message", "No matching credentials.")));

            if (!patient.getPassword().equals(login.getPassword()))
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        (Map.of("message", "No matching credentials.")));

            return ResponseEntity.ok(Map.of("token", tokenService.generateToken(login.getEmail())));
        } catch (Exception e) {
            logger.error("Exception in validatePatientLogin", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to validate patient login."));
        }
    }

    // wrap appointment filters in one method
    public ResponseEntity<Map<String, Object>> filterPatient(String name, String condition, String token) {
        try {
            Patient patient = patientRepository.findByEmail(tokenService.extractEmail(token));
            if (patient == null)
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                        (Map.of("message", "No matching patient.")));

            boolean hasName = name != null && !name.isBlank();
            boolean hasCondition = condition != null && !condition.isBlank();

            if (hasName && hasCondition) {
                return patientService.filterByDoctorAndCondition(condition, name, patient.getId());
            } else if (hasName) {
                return patientService.filterByDoctor(name, patient.getId());
            } else if (hasCondition) {
                return patientService.filterByCondition(condition, patient.getId());
            } else {
                return patientService.getPatientAppointment(patient.getId(), token);
            }
        } catch (Exception e) {
            logger.error("Exception in filterPatient", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    (Map.of("message", "Failed to filter patient.")));
        }
    }
}

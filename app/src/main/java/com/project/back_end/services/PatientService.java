package com.project.back_end.services;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.PatientRepository;

import com.project.back_end.DTO.AppointmentDTO;

@Service
public class PatientService {
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    private static final Logger logger = LoggerFactory.getLogger(PatientService.class);

    public PatientService(
            PatientRepository patientRepository,
            AppointmentRepository appointmentRepository,
            TokenService tokenService) {
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    @Transactional
    public int createPatient(Patient patient) {
        try {
            // entities have @Column(unique = true) for email/username
            patientRepository.save(patient);
            return 1;
        } catch (Exception e) {
            logger.error("Failed to create patient PatientService.createPatient", e);
            return 0;
        }
    }

    @Transactional
    public ResponseEntity<Map<String, Object>> getPatientAppointment(Long id, String token) {
        try {
            String email = tokenService.extractEmail(token);
            if (id != patientRepository.findByEmail(email).getId())
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("appointments", Collections.<Appointment>emptyList()));

            List<AppointmentDTO> asDTO = appointmentRepository.findByPatientId(id).stream().map(app -> mapToDTO(app))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(Map.of("appointments", asDTO));

        } catch (Exception e) {
            logger.error("Exception in getPatientAppointment", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to get patient appointment."));
        }
    }

    @Transactional
    public ResponseEntity<Map<String, Object>> filterByCondition(String condition, Long id) {
        try {
            List<Appointment> apps = appointmentRepository.findByPatientId(id);
            List<AppointmentDTO> filteredApps;
            if (condition.equalsIgnoreCase("past")) {
                filteredApps = apps.stream().filter(app -> app.getAppointmentTime().isBefore(LocalDateTime.now()))
                        .map(app -> mapToDTO(app))
                        .collect(Collectors.toList());

            } else if (condition.equalsIgnoreCase("future")) {
                filteredApps = apps.stream().filter(app -> app.getAppointmentTime().isAfter(LocalDateTime.now()))
                        .map(app -> mapToDTO(app))
                        .collect(Collectors.toList());

            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("message", "Invalid condition: " + condition));
            }
            return ResponseEntity.ok(Map.of("appointments", filteredApps));
        } catch (Exception e) {
            logger.error("Exception in filterByCondition", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to filter by condition."));
        }
    }

    @Transactional
    public ResponseEntity<Map<String, Object>> filterByDoctor(String name, Long patientId) {
        try {
            return ResponseEntity
                    .ok(Map.of("appointments", appointmentRepository.filterByDoctorNameAndPatientId(name, patientId)));
        } catch (Exception e) {
            logger.error("Exception in filterByDoctor", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to filter by doctor."));
        }
    }

    @Transactional
    public ResponseEntity<Map<String, Object>> filterByDoctorAndCondition(String condition, String name,
            Long patientId) {
        try {
            List<Appointment> apps = appointmentRepository.filterByDoctorNameAndPatientId(name, patientId);
            List<AppointmentDTO> filteredApps;

            if (condition.equalsIgnoreCase("past")) {
                filteredApps = apps.stream().filter(app -> app.getAppointmentTime().isBefore(LocalDateTime.now()))
                        .map(app -> mapToDTO(app))
                        .collect(Collectors.toList());

            } else if (condition.equalsIgnoreCase("future")) {
                filteredApps = apps.stream().filter(app -> app.getAppointmentTime().isAfter(LocalDateTime.now()))
                        .map(app -> mapToDTO(app))
                        .collect(Collectors.toList());

            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("message", "Invalid condition: " + condition));
            }
            return ResponseEntity.ok(Map.of("appointments", filteredApps));
        } catch (Exception e) {
            logger.error("Exception in filterByDoctorAndCondition", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to filter by doctor and condition."));
        }
    }

    public ResponseEntity<Map<String, Object>> getPatientDetails(String token) {
        try {
            Patient patient = patientRepository.findByEmail(tokenService.extractEmail(token));
            if (patient == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "No patient with matching email exists."));
            }
            return ResponseEntity.ok(Map.of("patient", patient));
        } catch (Exception e) {
            logger.error("Exception in getPatientDetails", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to get patient details."));
        }
    }

    private AppointmentDTO mapToDTO(Appointment app) {
        Patient patient = app.getPatient();
        Doctor doctor = app.getDoctor();
        return new AppointmentDTO(
                app.getId(),
                doctor.getId(),
                doctor.getName(),
                patient.getId(),
                patient.getName(),
                patient.getEmail(),
                patient.getPhone(),
                patient.getAddress(),
                app.getAppointmentTime(),
                app.getStatus());
    }
}

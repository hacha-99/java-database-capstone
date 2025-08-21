package com.project.back_end.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final TokenService tokenService;
    private final com.project.back_end.services.Service service;

    private static final Logger logger = LoggerFactory.getLogger(AppointmentService.class);

    public AppointmentService(
            AppointmentRepository appointmentRepository,
            PatientRepository patientRepository,
            DoctorRepository doctorRepository,
            TokenService tokenService,
            com.project.back_end.services.Service service) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.tokenService = tokenService;
        this.service = service;
    }

    @Transactional
    public int bookAppointment(Appointment appointment) {
        try {
            if (service.validateAppointment(appointment) == 1) {
                appointmentRepository.save(appointment);
                return 1;
            } else {
                return 0;
            }
        } catch (Exception e) {
            logger.error("Exception in bookAppointment", e);
            return 0;
        }
    }

    @Transactional
    public ResponseEntity<Map<String, String>> updateAppointment(Appointment appointment) {
        Map<String, String> response = new HashMap<>();
        try {
            Optional<Appointment> existingOptional = appointmentRepository.findById(appointment.getId());
            // check if db entry exists
            if (existingOptional.isEmpty()) {
                response.put("message", "No matching appointment found.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            Appointment existingApp = existingOptional.get();

            // check if sent data is identical
            if (existingApp.equals(appointment)) {
                response.put("message", "No changes detected.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            // check if patient IDs match
            if (!existingApp.getPatient().getId().equals(appointment.getPatient().getId())) {
                response.put("message", "Patient IDs do not match.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            // check if existing appointment is in the past
            if (existingApp.getAppointmentTime().isBefore(LocalDateTime.now())) {
                response.put("message", "Cannot update an appointment in the past");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            int validateAppResult = service.validateAppointment(appointment);
            // check whether appointment with that doctor at that time is available
            if (validateAppResult == -1) {
                response.put("message", "Doctor with specified ID does not exist.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);

            } else if (validateAppResult == 0) {
                response.put("message", "This time slot is unavailable.");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);

            } else if (validateAppResult == 1) {
                appointmentRepository.save(appointment);
                response.put("message", "Appointment saved.");
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "Could not process result of validateAppointment.");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }
        } catch (Exception e) {
            logger.error("Exception in updateAppointment", e);
            response.put("message", "Failed to update appointment.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Transactional
    public ResponseEntity<Map<String, String>> cancelAppointment(Long id, String token) {
        try {
            Map<String, String> response = new HashMap<>();

            // check if patient or appointment null/empty
            Patient patient = patientRepository.findByEmail(tokenService.extractEmail(token));
            Optional<Appointment> optApp = appointmentRepository.findById(id);
            if (patient == null || optApp.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        (Map.of("message", "Patient with given email or appointment with given id does not exist.")));
            }

            // check if id of patient matches with appointment patient id
            Appointment app = optApp.get();
            if (patient.getId() != app.getPatient().getId()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                        (Map.of("message", "Requesting patient id does not match patient id of appointment.")));
            }

            appointmentRepository.delete(app);
            response.put("message", "Appointment deleted.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Exception in cancelAppointment", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to cancel appointment."));
        }
    }

    @Transactional
    public ResponseEntity<Map<String, Object>> getAppointments(String patientName, LocalDate date, String token) {
        try {
            boolean hasPatientName = patientName != null && !patientName.isBlank();

            // check if doctor exists
            Doctor doc = doctorRepository.findByEmail(tokenService.extractEmail(token));
            if (doc == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Failed to find matching doctor."));
            }

            // act depending on whether patientname is empty or not
            List<Appointment> apps;
            if (hasPatientName) {
                apps = appointmentRepository.findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween(
                        doc.getId(), patientName.trim(), date.atStartOfDay(),
                        date.plusDays(1).atStartOfDay());
            } else {
                apps = appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(
                        doc.getId(), date.atStartOfDay(),
                        date.plusDays(1).atStartOfDay());
            }

            return ResponseEntity.ok(Map.of("appointments", apps.stream().map(app -> mapToDTO(app)).collect(Collectors.toList())));
        } catch (Exception e) {
            logger.error("Exception in getAppointments", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to get appointments."));
        }
    }

    @Transactional
    public ResponseEntity<Map<String, String>> changeStatus(int status, Long id) {
        try {
            appointmentRepository.updateStatus(status, id);
            return ResponseEntity.ok(Map.of("message", "Appointment status updated"));
        } catch (Exception e) {
            logger.error("Exception in changeStatus", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to change status."));
        }
    }

    public static AppointmentDTO mapToDTO(Appointment app) {
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
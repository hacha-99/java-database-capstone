package com.project.back_end.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Patient;
import com.project.back_end.services.PatientService;
import com.project.back_end.services.Service;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("${api.path}patient")
public class PatientController {

    @Autowired
    private PatientService patientService;

    @Autowired
    private Service service;

    // - Handles HTTP GET requests to retrieve patient details using a token.
    @GetMapping("/{token}")
    public ResponseEntity<?> getPatient(@PathVariable String token) {
        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "patient");
        if (!validation.getStatusCode().is2xxSuccessful()) {
            return validation;
        }
        return patientService.getPatientDetails(token);
    }

    // - Handles HTTP POST requests for patient registration.
    @PostMapping
    public ResponseEntity<?> createPatient(@RequestBody @Valid Patient patient) {

        boolean doesNotExist = service.validatePatient(patient);

        if (doesNotExist) {
            int createResult = patientService.createPatient(patient);
            if (createResult == 1) {
                return ResponseEntity.ok(Map.of("message", "Signup successful"));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("message", "Internal server error"));
            }
        }
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("message", "Patient with email id or phone no already exist"));
    }

    // - Handles HTTP POST requests for patient login.
    @PostMapping("/login")
    public ResponseEntity<?> loginPatient(@RequestBody Login login) {
        return service.validatePatientLogin(login);
    }

    // - Handles HTTP GET requests to fetch appointment details for a specific
    // patient.
    @GetMapping("/{id}/{user}/{token}")
    public ResponseEntity<?> getPatientAppointment(@PathVariable Long id, @PathVariable String user,
            @PathVariable String token) {

        ResponseEntity<Map<String, String>> validation = service.validateToken(token, user);
        if (!validation.getStatusCode().is2xxSuccessful()) {
            return validation;
        }

        return patientService.getPatientAppointment(id, token);
    }

    // - Handles HTTP GET requests to filter a patient's appointments based on
    // specific conditions.
    @GetMapping("/filter")
    public ResponseEntity<?> filterPatientAppointments(
            @RequestParam(required = false) String condition,
            @RequestParam(required = false) String name,
            @RequestParam(required = true) String token) {

        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "patient");
        if (!validation.getStatusCode().is2xxSuccessful()) {
            return validation;
        }

        return service.filterPatient(name, condition, token);
    }
}

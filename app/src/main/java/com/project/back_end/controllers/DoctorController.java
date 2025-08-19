package com.project.back_end.controllers;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Doctor;
import com.project.back_end.services.DoctorService;
import com.project.back_end.services.Service;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("${api.path}doctor")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private Service service;

    // - Handles HTTP GET requests to check a specific doctor’s availability on a
    // given date.
    @GetMapping("/availability/{user}/{doctorId}/{date}/{token}")
    public ResponseEntity<?> getDoctorAvailability(@PathVariable String user, @PathVariable Long doctorId,
            @PathVariable LocalDate date, @PathVariable String token) {

        ResponseEntity<Map<String, String>> validation = service.validateToken(token, user);
        if (!validation.getStatusCode().is2xxSuccessful()) {
            return validation;
        }
        return ResponseEntity.ok(Map.of("availability", doctorService.getDoctorAvailability(doctorId, date)));
    }

    // - Handles HTTP GET requests to retrieve a list of all doctors.
    @GetMapping
    public ResponseEntity<?> getDoctors() {
        return ResponseEntity.ok(Map.of("doctors", doctorService.getDoctors()));
    }

    // - Handles HTTP POST requests to register a new doctor.
    @PostMapping("/{token}")
    public ResponseEntity<?> saveDoctor(@RequestBody @Valid Doctor doctor, @PathVariable String token) {

        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "admin");
        if (!validation.getStatusCode().is2xxSuccessful()) {
            return validation;
        }

        int saveResult = doctorService.saveDoctor(doctor);
        switch (saveResult) {
            case 1:
                return ResponseEntity.ok().body(Map.of("message", "Doctor added to db"));
            case -1:
                return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "Doctor already exists"));
            case 0:
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("message", "Some internal error occurred"));
            default:
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("message", "Defaulted: Some internal error occured"));
        }
    }

    // - Handles HTTP POST requests for doctor login.
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> doctorLogin(@RequestBody Login login) {
        return doctorService.validateDoctor(login);
    }

    // TODO: check for updating functionality on frontend (fetch request is
    // implemented in doctorServices.js)"
    // - Handles HTTP PUT requests to update an existing doctor's information.
    @PutMapping("/{token}")
    public ResponseEntity<?> updateDoctor(@RequestBody @Valid Doctor doctor, @PathVariable String token) {

        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "admin");
        if (!validation.getStatusCode().is2xxSuccessful()) {
            return validation;
        }

        int updateResult = doctorService.updateDoctor(doctor);
        switch (updateResult) {
            case 1:
                return ResponseEntity.ok().body(Map.of("message", "Doctor updated"));
            case -1:
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Doctor not found"));
            case 0:
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("message", "Some internal error occurred"));
            default:
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("message", "Defaulted: Some internal error occured"));
        }
    }

    // - Handles HTTP DELETE requests to remove a doctor by ID.
    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<?> deleteDoctor(@PathVariable Long id, @PathVariable String token) {

        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "admin");
        if (!validation.getStatusCode().is2xxSuccessful()) {
            return validation;
        }

        int deleteResult = doctorService.deleteDoctor(id);
        switch (deleteResult) {
            case 1:
                return ResponseEntity.ok().body(Map.of("message", "Doctor deleted successfully"));
            case -1:
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Doctor not found with id"));
            case 0:
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("message", "Some internal error occurred"));
            default:
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("message", "Defaulted: Some internal error occured"));
        }
    }

    // - Handles HTTP GET requests to filter doctors based on name, time, and
    // specialty.
    @GetMapping("/filter")
    public ResponseEntity<?> filter(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String time,
            @RequestParam(required = false) String specialty) {

        return ResponseEntity.ok(Map.of(
                "doctors", service.filterDoctor(name, specialty, time)));
    }
}

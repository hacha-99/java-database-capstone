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
import org.springframework.web.bind.annotation.RestController;

import com.project.back_end.models.Appointment;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private Service service;

    @GetMapping("/{date}/{patientName}/{token}")
    public ResponseEntity<?> getAppointments(@PathVariable LocalDate date, @PathVariable String patientName, @PathVariable String token){
        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "doctor");
        if(!validation.getStatusCode().is2xxSuccessful()){
            return validation;
        }
        return appointmentService.getAppointments(patientName, date, token);
    }

    @PostMapping("/{token}")
    public ResponseEntity<?> bookAppointment(@PathVariable String token, @RequestBody Appointment appointment) {
        
        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "patient");
        if(!validation.getStatusCode().is2xxSuccessful()){
            return validation;
        }

        int bookingResult = appointmentService.bookAppointment(appointment);
        if(bookingResult != 1){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Invalid booking request."));
        }
        return ResponseEntity.ok().build();
    }

    @PutMapping("/token")
    public ResponseEntity<?> updateAppointment(@PathVariable String token, @RequestBody Appointment appointment){

        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "patient");
        if(!validation.getStatusCode().is2xxSuccessful()){
            return validation;
        }
        return appointmentService.updateAppointment(appointment);
    }

    // TODO: check for canceling functionality on frontend
    @DeleteMapping("/{appointmentId}/{token}")
    public ResponseEntity<?> cancelAppointment(@PathVariable Long appointmendId, @PathVariable String token){

        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "patient");
        if(!validation.getStatusCode().is2xxSuccessful()){
            return validation;
        }
        return appointmentService.cancelAppointment(appointmendId, token);
    }
}

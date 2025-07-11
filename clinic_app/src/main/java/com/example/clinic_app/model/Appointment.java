package com.example.clinic_app.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
public class Appointment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    @NotNull
    private Doctor doctor;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    @NotNull
    private Patient patient;

    @NotNull
    @Future(message = "Appointment time must be in the future")
    private LocalDateTime appointmentTime;

    @Min(0)
    @Max(2)
    @NotNull
    private int status; // 0 -> scheduled, 1 -> completed, 2 -> cancelled

    @Transient
    public LocalDateTime getEndTime(){
        return appointmentTime.plusHours(1);
    }

    @Transient
    public LocalDate getAppointmentDate(){
        return appointmentTime.toLocalDate();
    }

    @Transient
    public LocalTime getAppointmentTimeOnly(){
        return appointmentTime.toLocalTime();
    }

    @Transient
    public Long getAppointmentYearsElapsed(){
        return (appointmentTime.isAfter(LocalDateTime.now()) ? 0 :
            ChronoUnit.YEARS.between(appointmentTime, LocalDateTime.now()));
            // return 0 if appointment is in future, otherwise years elapsed
    }
}

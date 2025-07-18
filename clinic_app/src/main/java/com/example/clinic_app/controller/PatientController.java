package com.example.clinic_app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/patient")
public class PatientController {

    @GetMapping("/dashboard")
    public String adminDashboard() {
        return "../static/pages/patientDashboard"; // not a th template
    }
}

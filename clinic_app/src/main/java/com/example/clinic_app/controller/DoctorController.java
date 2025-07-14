package com.example.clinic_app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/doctor")
public class DoctorController {

    @GetMapping("/dashboard")
    public String adminDashboard() {
        return "doctor/doctorDashboard";
    }
}
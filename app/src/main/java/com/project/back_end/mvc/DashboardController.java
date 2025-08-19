package com.project.back_end.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.project.back_end.services.TokenService;

@Controller
public class DashboardController {

    @Autowired
    private TokenService tokenService;

    @GetMapping("/adminDashboard/{token}")
    public String adminDashboard(@PathVariable String token) {
        if (tokenService.validateToken(token, "admin")) {
            return "admin/adminDashboard";
        }
        else {
            return "redirect:/";
        }
    }

    @GetMapping("/doctorDashboard/{token}")
    public String doctorDashboard(@PathVariable String token) {
        if (tokenService.validateToken(token, "doctor")) {
            return "doctor/doctorDashboard";
        }
        else {
            return "redirect:/";
        }
    }
}


package com.project.back_end.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.back_end.models.Admin;
import com.project.back_end.services.Service;

import jakarta.validation.Valid;

@RestController
@RequestMapping("${api.path}admin")
public class AdminController {

    @Autowired
    private Service service;

    // - Handles HTTP POST requests for admin login functionality.
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> adminLogin(@RequestBody @Valid Admin admin) {
        return service.validateAdmin(admin);
    }
}

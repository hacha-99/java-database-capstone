package com.project.back_end.services;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.project.back_end.models.Prescription;
import com.project.back_end.repo.PrescriptionRepository;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class PrescriptionService {
    
    PrescriptionRepository prescriptionRepository;

    private static final Logger logger = LoggerFactory.getLogger(PrescriptionService.class);

    public PrescriptionService(PrescriptionRepository prescriptionRepository){
        this.prescriptionRepository = prescriptionRepository;
    }

    public ResponseEntity<Map<String, String>> savePrescription(Prescription presc){
        try {
            prescriptionRepository.save(presc);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Prescription saved."));
        } catch (Exception e) {
            logger.error("Exception in savePrescription.", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Failed to save prescription."));
        }
    }

    public ResponseEntity<Map<String, Object>> getPrescription(Long appointmentId){
        try {
            return ResponseEntity.ok(Map.of("prescription", prescriptionRepository.findByAppointmentId(appointmentId)));
        } catch (Exception e) {
            logger.error("Exception in getPrescription.", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Failed to get prescription."));
        }
    }
}

package com.project.back_end.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.*;

import java.util.List;

@Document(collection = "prescriptions")
public class Prescription {

    @Id
    private String id;

    @NotNull
    @Size(min = 3, max = 100)
    private String patientName;

    @NotNull
    private Long appointmentId;

    @NotNull
    @Size(min = 1)
    private List<Medication> medication;

    public Prescription() {
    }

    public Prescription(String patientName, Long appointmentId, List<Medication> medication) {
        this.patientName = patientName;
        this.appointmentId = appointmentId;
        this.medication = medication;
    }

    // Getter und Setter

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
    }

    public List<Medication> getMedication() {
        return medication;
    }

    public void setMedication(List<Medication> medication) {
        this.medication = medication;
    }

    // inner class for medication
    public static class Medication {
        @NotNull
        @Size(min = 3, max = 100)
        private String name;

        @NotNull
        @Size(min = 3, max = 20)
        private String dosage;

        @Size(max = 200)
        private String doctorNotes;

        private int refillCount;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDosage() {
            return dosage;
        }

        public void setDosage(String dosage) {
            this.dosage = dosage;
        }

        public String getDoctorNotes() {
            return doctorNotes;
        }

        public void setDoctorNotes(String doctorNotes) {
            this.doctorNotes = doctorNotes;
        }

        public int getRefillCount() {
            return refillCount;
        }

        public void setRefillCount(int refillCount) {
            this.refillCount = refillCount;
        }
    }
}

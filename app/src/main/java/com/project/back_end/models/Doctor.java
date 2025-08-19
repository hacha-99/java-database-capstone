package com.project.back_end.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

@Entity
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 3, max = 100)
    private String name;

    @Past
    private LocalDate dob;

    @NotBlank
    @Size(min = 3, max = 50)
    private String specialty;

    @Email
    @NotBlank
    @Column(unique = true)
    private String email;

    @NotBlank
    @Size(min = 6, max = 100)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) // control serialization
    private String password;

    @NotBlank
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be 10 digits")
    private String phone;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> availableTimes;
    // Each time slot is represented as a string (e.g., "09:00-10:00", "10:00-11:00").

    @Min(0)
    @Max(2)
    private Integer gender; // 0 = Male, 1 = Female, 2 = Other; primitive types (including int) cant be null, use Integer

    @Size(max = 255)
    private String address;

    @Column(columnDefinition = "boolean default true")
    private boolean active;
    

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<String> getAvailableTimes() {
        return availableTimes;
    }

    public void setAvailableTimes(List<String> availableTimes) {
        this.availableTimes = availableTimes;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}

package com.project.back_end.services;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;

@Service
public class DoctorService {
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    private static final Logger logger = LoggerFactory.getLogger(DoctorService.class);

    public DoctorService(
            DoctorRepository doctorRepository,
            AppointmentRepository appointmentRepository,
            TokenService tokenService) {
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    @Transactional
    public List<String> getDoctorAvailability(Long doctorId, LocalDate date) {
        try {
            Doctor doctor = doctorRepository.findById(doctorId)
                    .orElseThrow(
                            () -> new NoSuchElementException("Error :: getDoctorAvailability :: Doctor not found."));

            List<String> allSlots = doctor.getAvailableTimes();

            if (allSlots == null || allSlots.isEmpty()) {
                return Collections.<String>emptyList();
            }

            // already booked appointments for specified day and doctor
            List<Appointment> bookedAppointments = appointmentRepository
                    .findByDoctorIdAndAppointmentTimeBetween(
                            doctorId,
                            date.atStartOfDay(),
                            date.plusDays(1).atStartOfDay());

            // extract slots of booked appointments
            List<String> bookedSlots = bookedAppointments.stream()
                    .map(app -> {
                        String start = app.getAppointmentTimeOnly().toString().substring(0, 5);
                        String end = app.getEndTime().toString().substring(0, 5);
                        return (start + "-" + end);
                    })
                    .collect(Collectors.toList());

            // all slots minus booked slots equals available slots
            List<String> availableSlots = allSlots.stream()
                    .filter(slot -> !bookedSlots.contains(slot))
                    .collect(Collectors.toList());

            return availableSlots;
        } catch (Exception e) {
            logger.error("Exception in getDoctorAvailability", e);
            return Collections.<String>emptyList();
        }

    }

    @Transactional
    public int saveDoctor(Doctor doctor) {
        try {
            if (doctorRepository.existsByEmail(doctor.getEmail()))
                return -1;
            doctorRepository.save(doctor);
            return 1;
        } catch (Exception e) {
            logger.error("Exception in saveDoctor", e);
            return 0;
        }
    }

    @Transactional
    public int updateDoctor(Doctor doctor) {
        try {
            if (!doctorRepository.existsById(doctor.getId()))
                return -1;
            doctorRepository.save(doctor);
            return 1;
        } catch (Exception e) {
            logger.error("Exception in updateDoctor", e);
            return 0;
        }
    }

    @Transactional
    public List<Doctor> getDoctors() {
        try {
            return doctorRepository.findAll();
        } catch (Exception e) {
            logger.error("Exception in getDoctors", e);
            return Collections.<Doctor>emptyList();
        }
    }

    @Transactional
    public int deleteDoctor(Long doctorId) {
        try {
            if (!doctorRepository.existsById(doctorId))
                return -1;
            appointmentRepository.deleteAllByDoctorId(doctorId);
            doctorRepository.deleteById(doctorId);
            return 1;
        } catch (Exception e) {
            logger.error("Exception in deleteDoctor", e);
            return 0;
        }
    }

    // check for doctor login credentials
    @Transactional
    public ResponseEntity<Map<String, String>> validateDoctor(Login login) {
        try {
            Doctor doctor = doctorRepository.findByEmail(login.getEmail());
            if (doctor == null)
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        (Map.of("message", "No matching credentials.")));

            if (!doctor.getPassword().equals(login.getPassword()))
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        (Map.of("message", "No matching credentials.")));

            return ResponseEntity.ok(Map.of("token", tokenService.generateToken(login.getEmail())));
        } catch (Exception e) {
            logger.error("Exception in validateDoctor", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to validate doctor."));
        }
    }

    @Transactional
    public Map<String, Object> findDoctorByName(String name) {
        try {
            return Map.of("doctors", doctorRepository.findByNameLike(name));
        } catch (Exception e) {
            logger.error("Exception in findDoctorByName", e);
            return Map.of("doctors", Collections.<Doctor>emptyList());
        }
    }

    @Transactional
    public Map<String, Object> filterDoctorsByNameSpecialtyAndTime(String name, String specialty, String amOrPm) {
        try {
            return Map.of("doctors",
                    filterDoctorByTime(doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name,
                            specialty), amOrPm));
        } catch (Exception e) {
            logger.error("Exception in filterDoctorsByNameSpecialtyAndTime", e);
            return Map.of("doctors", Collections.<Doctor>emptyList());
        }
    }

    @Transactional
    private List<Doctor> filterDoctorByTime(List<Doctor> doctors, String amOrPm) {
        try {
            if (amOrPm.equalsIgnoreCase("pm")) {
                // only include the doctors who have at least one appointment post meridiem or
                // ante meridiem
                // stream doctors availableTime, map to LocalTime (e.g. 09:00) and check for
                // matches (before or after noon)
                // anyMatch delivers boolean value for filter method to decide whether to
                // include or exclude doctor
                return doctors.stream()
                        .filter(doctor -> doctor.getAvailableTimes().stream()
                                .map(slot -> LocalTime.parse(slot.substring(0, 5),
                                        DateTimeFormatter.ofPattern("HH:mm")))
                                .anyMatch(time -> time.isAfter(LocalTime.NOON)))
                        .collect(Collectors.toList());
            } else if (amOrPm.equalsIgnoreCase("am")) {
                return doctors.stream()
                        .filter(doctor -> doctor.getAvailableTimes().stream()
                                .map(slot -> LocalTime.parse(slot.substring(0, 5),
                                        DateTimeFormatter.ofPattern("HH:mm")))
                                .anyMatch(time -> time.isBefore(LocalTime.NOON)))
                        .collect(Collectors.toList());
            } else {
                return Collections.<Doctor>emptyList();
            }
        } catch (Exception e) {
            logger.error("Exception in filterDoctorByTime", e);
            return Collections.<Doctor>emptyList();
        }
    }

    @Transactional
    public Map<String, Object> filterDoctorsByTime(String amOrPm) {
        try {
            return Map.of("doctors", filterDoctorByTime(doctorRepository.findAll(), amOrPm));
        } catch (Exception e) {
            logger.error("Exception in filterDoctorsByTime", e);
            return Map.of("doctors", Collections.<Doctor>emptyList());
        }
    }

    @Transactional
    public Map<String, Object> filterDoctorByNameAndTime(String name, String amOrPm) {
        try {
            return Map.of("doctors", filterDoctorByTime(doctorRepository.findByNameLike(name), amOrPm));
        } catch (Exception e) {
            logger.error("Exception in filterDoctorByNameAndTime", e);
            return Map.of("doctors", Collections.<Doctor>emptyList());
        }
    }

    @Transactional
    public Map<String, Object> filterDoctorByNameAndSpecialty(String name, String specialty) {
        try {
            return Map.of("doctors",
                    doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specialty));
        } catch (Exception e) {
            logger.error("Exception in filterDoctorByNameAndSpecialty", e);
            return Map.of("doctors", Collections.<Doctor>emptyList());
        }
    }

    @Transactional
    public Map<String, Object> filterDoctorByTimeAndSpecialty(String amOrPm, String specialty) {
        try {
            return Map.of("doctors",
                    filterDoctorByTime(doctorRepository.findBySpecialtyIgnoreCase(specialty), amOrPm));
        } catch (Exception e) {
            logger.error("Exception in filterDoctorByTimeAndSpecialty", e);
            return Map.of("doctors", Collections.<Doctor>emptyList());
        }
    }

    @Transactional
    public Map<String, Object> filterDoctorBySpecialty(String specialty) {
        try {
            return Map.of("doctors", doctorRepository.findBySpecialtyIgnoreCase(specialty));
        } catch (Exception e) {
            logger.error("Exception in filterDoctorBySpecialty", e);
            return Map.of("doctors", Collections.<Doctor>emptyList());
        }
    }
}

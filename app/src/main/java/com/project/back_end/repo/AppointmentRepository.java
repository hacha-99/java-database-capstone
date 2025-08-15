package com.project.back_end.repo;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.project.back_end.models.Appointment;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

      @Query("""
                  SELECT a FROM Appointment a
                  LEFT JOIN FETCH a.doctor d
                  LEFT JOIN FETCH d.availableTimes
                  WHERE d.id = :doctorId
                  AND a.appointmentTime BETWEEN :start AND :end
                  """)
      List<Appointment> findByDoctorIdAndAppointmentTimeBetween(
                  @Param("doctorId") Long doctorId,
                  @Param("start") LocalDateTime start,
                  @Param("end") LocalDateTime end);

      // underscore because name is in patient; not needed for id because thats the
      // reference that will be present in the table
      @Query("""
                  SELECT a from Appointment a
                  LEFT JOIN FETCH a.doctor d
                  LEFT JOIN FETCH a.patient p
                  WHERE d.id = :doctorId
                  AND LOWER(p.name) LIKE LOWER(CONCAT('%', :patientName, '%'))
                  AND a.appointmentTime BETWEEN :start AND :end
                  """)
      List<Appointment> findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween(
                  @Param("doctorId") Long doctorId,
                  @Param("patientName") String patientName,
                  @Param("start") LocalDateTime start,
                  @Param("end") LocalDateTime end);

      @Modifying // for insert, delete, update
      @Transactional // modify all or nothing
      void deleteAllByDoctorId(Long doctorId);

      List<Appointment> findByPatientId(Long patientId);

      List<Appointment> findByPatient_IdAndStatusOrderByAppointmentTimeAsc(Long patientId, int status);

      @Query("""
                  SELECT a FROM Appointment a
                  JOIN FETCH a.doctor d
                  WHERE a.patient.id = :patientId
                  AND LOWER(d.name) LIKE LOWER(CONCAT('%', :doctorName, '%'))
                  """)
      List<Appointment> filterByDoctorNameAndPatientId(
                  @Param("doctorName") String doctorName,
                  @Param("patientId") Long patientId);

      @Query("""
                  SELECT a FROM Appointment a
                  JOIN FETCH a.doctor d
                  WHERE a.patient.id = :patientId
                  AND LOWER(d.name) LIKE LOWER(CONCAT('%', :doctorName, '%'))
                  AND a.status = :status
                  """)
      List<Appointment> filterByDoctorNameAndPatientIdAndStatus(
                  @Param("doctorName") String doctorName,
                  @Param("patientId") Long patientId,
                  @Param("status") int status);

    //   @Modifying
    //   @Transactional
    //   void updateStatus(int status, long id);
}

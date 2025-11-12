package com.kops.sem_tracker.repository;

import com.kops.sem_tracker.entyties.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    List<Attendance> findByStudentId(String studentId);

    List<Attendance> findByStudentIdAndModuleName(String studentId, String moduleName);

    List<Attendance> findByStudentIdAndAttendanceDateBetween(String studentId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT a FROM Attendance a WHERE a.studentId = :studentId AND a.moduleName = :moduleName AND a.attendanceDate BETWEEN :startDate AND :endDate")
    List<Attendance> findAttendanceByStudentAndModuleAndDateRange(
            @Param("studentId") String studentId,
            @Param("moduleName") String moduleName,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT COALESCE(SUM(a.attendedHours), 0) FROM Attendance a WHERE a.studentId = :studentId AND a.moduleName = :moduleName")
    Double getTotalAttendedHoursByStudentAndModule(@Param("studentId") String studentId, @Param("moduleName") String moduleName);
}

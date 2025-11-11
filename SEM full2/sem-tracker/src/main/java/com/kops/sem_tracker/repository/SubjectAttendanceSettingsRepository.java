package com.kops.sem_tracker.repository;

import com.kops.sem_tracker.entyties.SubjectAttendanceSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectAttendanceSettingsRepository extends JpaRepository<SubjectAttendanceSettings, Long> {
    
    List<SubjectAttendanceSettings> findByStudentId(String studentId);
    
    Optional<SubjectAttendanceSettings> findByStudentIdAndSubjectName(String studentId, String subjectName);
    
    @Query("SELECT s FROM SubjectAttendanceSettings s WHERE s.studentId = :studentId ORDER BY s.subjectName")
    List<SubjectAttendanceSettings> findByStudentIdOrderBySubjectName(@Param("studentId") String studentId);
    
    boolean existsByStudentIdAndSubjectName(String studentId, String subjectName);
}

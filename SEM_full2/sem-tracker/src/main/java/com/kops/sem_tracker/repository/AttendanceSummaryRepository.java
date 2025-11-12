package com.kops.sem_tracker.repository;

import com.kops.sem_tracker.entyties.AttendanceSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceSummaryRepository extends JpaRepository<AttendanceSummary, Long> {

    Optional<AttendanceSummary> findByStudentIdAndModuleName(String studentId, String moduleName);

    List<AttendanceSummary> findByStudentId(String studentId);

    boolean existsByStudentIdAndModuleName(String studentId, String moduleName);
}
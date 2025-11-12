package com.kops.sem_tracker.repository;

import com.kops.sem_tracker.entyties.StudentProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudentProgressRepository extends JpaRepository<StudentProgress, Long> {
    Optional<StudentProgress> findByStudentIdAndSubjectId(String studentId, Long subjectId);
    List<StudentProgress> findByStudentId(String studentId);
    boolean existsByStudentIdAndSubjectId(String studentId, Long subjectId);
}

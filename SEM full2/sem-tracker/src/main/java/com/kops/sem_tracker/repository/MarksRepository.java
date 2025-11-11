package com.kops.sem_tracker.repository;

import com.kops.sem_tracker.entyties.Marks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface MarksRepository extends JpaRepository<Marks, Long> {
    List<Marks> findByStudentId(String studentId);
    List<Marks> findByStudentIdAndSubjectId(String studentId, Long subjectId);
    List<Marks> findByStudentIdAndAssessmentType(String studentId, String assessmentType);
    List<Marks> findByStudentIdAndAssessmentDateBetween(String studentId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT m FROM Marks m WHERE m.studentId = :studentId AND m.subject.id = :subjectId ORDER BY m.assessmentDate DESC")
    List<Marks> findLatestByStudentAndSubject(@Param("studentId") String studentId, @Param("subjectId") Long subjectId);

    @Query("SELECT AVG(m.obtainedMarks * 100.0 / m.totalMarks) FROM Marks m WHERE m.studentId = :studentId AND m.subject.id = :subjectId")
    Double findAveragePercentageByStudentAndSubject(@Param("studentId") String studentId, @Param("subjectId") Long subjectId);

    @Query("SELECT COUNT(m) FROM Marks m WHERE m.studentId = :studentId AND m.obtainedMarks >= m.passMarks")
    Long countPassedAssessments(@Param("studentId") String studentId);

    @Query("SELECT COUNT(m) FROM Marks m WHERE m.studentId = :studentId AND m.obtainedMarks < m.passMarks")
    Long countFailedAssessments(@Param("studentId") String studentId);
}

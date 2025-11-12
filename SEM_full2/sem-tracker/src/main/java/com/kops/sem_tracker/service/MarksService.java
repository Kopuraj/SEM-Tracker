package com.kops.sem_tracker.service;

import com.kops.sem_tracker.entyties.Marks;
import com.kops.sem_tracker.entyties.StudentProgress;
import com.kops.sem_tracker.entyties.Subject;
import com.kops.sem_tracker.repository.MarksRepository;
import com.kops.sem_tracker.repository.StudentProgressRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class MarksService {
    private final MarksRepository marksRepository;
    private final StudentProgressRepository progressRepository;
    private final SubjectService subjectService;

    public MarksService(MarksRepository marksRepository,
                        StudentProgressRepository progressRepository,
                        SubjectService subjectService) {
        this.marksRepository = marksRepository;
        this.progressRepository = progressRepository;
        this.subjectService = subjectService;
    }

    @Transactional
    public Marks save(Marks marks) {
        Marks savedMarks = marksRepository.save(marks);
        updateStudentProgress(savedMarks);
        return savedMarks;
    }

    public List<Marks> getAll() {
        return marksRepository.findAll();
    }

    public Optional<Marks> getById(Long id) {
        return marksRepository.findById(id);
    }

    public List<Marks> getByStudentId(String studentId) {
        return marksRepository.findByStudentId(studentId);
    }

    public List<Marks> getByStudentIdAndSubjectId(String studentId, Long subjectId) {
        return marksRepository.findByStudentIdAndSubjectId(studentId, subjectId);
    }

    public List<Marks> getByStudentIdAndAssessmentType(String studentId, String assessmentType) {
        return marksRepository.findByStudentIdAndAssessmentType(studentId, assessmentType);
    }

    public Marks update(Long id, Marks newData) {
        return marksRepository.findById(id)
                .map(existing -> {
                    existing.setStudentId(newData.getStudentId());
                    existing.setSubject(newData.getSubject());
                    existing.setObtainedMarks(newData.getObtainedMarks());
                    existing.setAssessmentType(newData.getAssessmentType());
                    existing.setAssessmentDate(newData.getAssessmentDate());
                    existing.setRemarks(newData.getRemarks());

                    Marks updated = marksRepository.save(existing);
                    updateStudentProgress(updated);
                    return updated;
                })
                .orElseGet(() -> {
                    newData.setId(id);
                    Marks saved = marksRepository.save(newData);
                    updateStudentProgress(saved);
                    return saved;
                });
    }

    public void delete(Long id) {
        marksRepository.deleteById(id);
    }

    public double getStudentAveragePercentage(String studentId) {
        List<Marks> allMarks = marksRepository.findByStudentId(studentId);
        if (allMarks.isEmpty()) return 0;

        double totalPercentage = 0;
        for (Marks mark : allMarks) {
            totalPercentage += mark.calculatePercentage();
        }

        return totalPercentage / allMarks.size();
    }

    public double getStudentSubjectAveragePercentage(String studentId, Long subjectId) {
        Double average = marksRepository.findAveragePercentageByStudentAndSubject(studentId, subjectId);
        return average != null ? average : 0;
    }

    public Object[] getPassFailStatistics(String studentId) {
        Long passed = marksRepository.countPassedAssessments(studentId);
        Long failed = marksRepository.countFailedAssessments(studentId);
        Long total = passed + failed;

        double passPercentage = total > 0 ? (passed * 100.0) / total : 0;
        double failPercentage = total > 0 ? (failed * 100.0) / total : 0;

        return new Object[]{passed, failed, total, passPercentage, failPercentage};
    }

    public List<Marks> getRecentAssessments(String studentId, int limit) {
        List<Marks> allMarks = marksRepository.findByStudentId(studentId);
        return allMarks.stream()
                .sorted((m1, m2) -> m2.getAssessmentDate().compareTo(m1.getAssessmentDate()))
                .limit(limit)
                .toList();
    }

    private void updateStudentProgress(Marks marks) {
        String studentId = marks.getStudentId();
        Subject subject = marks.getSubject();

        Optional<StudentProgress> progressOpt = progressRepository.findByStudentIdAndSubjectId(studentId, subject.getId());

        StudentProgress progress;
        if (progressOpt.isPresent()) {
            progress = progressOpt.get();
        } else {
            progress = new StudentProgress(studentId, subject);
        }

        // GET VALUES SAFELY
        int obtainedMarks = marks.getObtainedMarks();
        int totalMarks = Math.max(1, marks.getTotalMarks()); // Ensure at least 1
        int passMarks = marks.getPassMarks();

        // VALIDATE VALUES
        obtainedMarks = Math.max(0, Math.min(obtainedMarks, totalMarks));

        progress.updateProgress(obtainedMarks, totalMarks, passMarks);
        progressRepository.save(progress);
    }
}
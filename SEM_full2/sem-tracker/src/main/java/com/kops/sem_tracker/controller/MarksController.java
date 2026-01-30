package com.kops.sem_tracker.controller;

import com.kops.sem_tracker.entyties.Marks;
import com.kops.sem_tracker.service.MarksService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/marks")
@CrossOrigin(origins = "*")
public class MarksController {
    private final MarksService marksService;

    public MarksController(MarksService marksService) {
        this.marksService = marksService;
    }

    @PostMapping
    public ResponseEntity<Marks> create(@RequestBody Marks marks) {
        return ResponseEntity.ok(marksService.save(marks));
    }

    @GetMapping
    public ResponseEntity<List<Marks>> getAll() {
        return ResponseEntity.ok(marksService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Marks> getById(@PathVariable Long id) {
        Optional<Marks> marks = marksService.getById(id);
        return marks.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<Marks>> getByStudentId(@PathVariable String studentId) {
        return ResponseEntity.ok(marksService.getByStudentId(studentId));
    }

    @GetMapping("/student/{studentId}/subject/{subjectId}")
    public ResponseEntity<List<Marks>> getByStudentAndSubject(
            @PathVariable String studentId, @PathVariable Long subjectId) {
        return ResponseEntity.ok(marksService.getByStudentIdAndSubjectId(studentId, subjectId));
    }

    @GetMapping("/student/{studentId}/type/{assessmentType}")
    public ResponseEntity<List<Marks>> getByStudentAndType(
            @PathVariable String studentId, @PathVariable String assessmentType) {
        return ResponseEntity.ok(marksService.getByStudentIdAndAssessmentType(studentId, assessmentType));
    }

    // ✅ For quizzes: compute pass status and how many more marks needed
    @GetMapping("/student/{studentId}/quiz/needed")
    public ResponseEntity<Map<String, Object>> getQuizPassGap(
            @PathVariable String studentId,
            @RequestParam int obtained,
            @RequestParam int passMarks,
            @RequestParam int totalMarks) {
        int moreNeeded = Math.max(0, passMarks - obtained);
        double percentage = totalMarks > 0 ? (obtained * 100.0) / totalMarks : 0;
        double pctNeeded = totalMarks > 0 ? (moreNeeded * 100.0) / totalMarks : 0;
        return ResponseEntity.ok(Map.of(
                "studentId", studentId,
                "obtained", obtained,
                "passMarks", passMarks,
                "totalMarks", totalMarks,
                "passed", obtained >= passMarks,
                "moreMarksNeeded", moreNeeded,
                "currentPercentage", percentage,
                "percentageNeededToPass", pctNeeded
        ));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Marks> update(@PathVariable Long id, @RequestBody Marks newData) {
        return ResponseEntity.ok(marksService.update(id, newData));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        marksService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/student/{studentId}/average")
    public ResponseEntity<Map<String, Object>> getStudentAverage(@PathVariable String studentId) {
        double average = marksService.getStudentAveragePercentage(studentId);
        return ResponseEntity.ok(Map.of(
                "studentId", studentId,
                "averagePercentage", average,
                "grade", calculateGrade(average)
        ));
    }

    @GetMapping("/student/{studentId}/subject/{subjectId}/average")
    public ResponseEntity<Map<String, Object>> getStudentSubjectAverage(
            @PathVariable String studentId, @PathVariable Long subjectId) {
        double average = marksService.getStudentSubjectAveragePercentage(studentId, subjectId);
        return ResponseEntity.ok(Map.of(
                "studentId", studentId,
                "subjectId", subjectId,
                "averagePercentage", average,
                "grade", calculateGrade(average)
        ));
    }

    @GetMapping("/student/{studentId}/statistics")
    public ResponseEntity<Map<String, Object>> getStudentStatistics(@PathVariable String studentId) {
        Object[] stats = marksService.getPassFailStatistics(studentId);
        return ResponseEntity.ok(Map.of(
                "studentId", studentId,
                "passedAssessments", stats[0],
                "failedAssessments", stats[1],
                "totalAssessments", stats[2],
                "passPercentage", stats[3],
                "failPercentage", stats[4]
        ));
    }

    @GetMapping("/student/{studentId}/recent")
    public ResponseEntity<List<Marks>> getRecentAssessments(
            @PathVariable String studentId,
            @RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(marksService.getRecentAssessments(studentId, limit));
    }

    @GetMapping("/student/{studentId}/progress")
    public ResponseEntity<Map<String, Object>> getStudentProgress(@PathVariable String studentId) {
        double average = marksService.getStudentAveragePercentage(studentId);
        Object[] stats = marksService.getPassFailStatistics(studentId);

        return ResponseEntity.ok(Map.of(
                "studentId", studentId,
                "overallAverage", average,
                "overallGrade", calculateGrade(average),
                "passedAssessments", stats[0],
                "failedAssessments", stats[1],
                "totalAssessments", stats[2],
                "passPercentage", stats[3],
                "failPercentage", stats[4],
                "improvementSuggestions", generateImprovementSuggestions(average, (Long) stats[1])
        ));
    }

    private String calculateGrade(double percentage) {
        if (percentage >= 90) return "A+";
        if (percentage >= 80) return "A";
        if (percentage >= 70) return "B+";
        if (percentage >= 60) return "B";
        if (percentage >= 50) return "C+";
        if (percentage >= 40) return "C";
        return "F";
    }

    private List<String> generateImprovementSuggestions(double average, Long failedAssessments) {
        java.util.List<String> suggestions = new java.util.ArrayList<>();

        if (average < 40) {
            suggestions.add("Focus on fundamental concepts and seek extra help from instructors");
        } else if (average < 60) {
            suggestions.add("Practice more problems and review previous assessments");
        } else if (average < 75) {
            suggestions.add("Work on time management and exam strategies");
        }

        if (failedAssessments > 0) {
            suggestions.add("Review failed assessments to understand patterns in mistakes");
            suggestions.add("Consider forming study groups for difficult subjects");
        }

        if (suggestions.isEmpty()) {
            suggestions.add("Maintain your current study habits - they're working well!");
        }

        return suggestions;
    }
}
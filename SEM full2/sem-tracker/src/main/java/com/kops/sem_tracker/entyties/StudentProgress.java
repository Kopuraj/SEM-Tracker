package com.kops.sem_tracker.entyties;

import jakarta.persistence.*;

@Entity
@Table(name = "student_progress")
public class StudentProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String studentId;

    @ManyToOne
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    private double averagePercentage;

    private int totalAssessments;

    private int passedAssessments;

    private int failedAssessments;

    private double improvementPercentage;

    // Constructors
    public StudentProgress() {}

    public StudentProgress(String studentId, Subject subject) {
        this.studentId = studentId;
        this.subject = subject;
        this.averagePercentage = 0;
        this.totalAssessments = 0;
        this.passedAssessments = 0;
        this.failedAssessments = 0;
        this.improvementPercentage = 0;
    }

    // Method to update progress
    public void updateProgress(int obtainedMarks, int totalMarks, int passMarks) {
        totalAssessments++;

        // SAFE CALCULATION - Prevent Infinity/NaN
        double percentage = 0;
        if (totalMarks > 0) {
            percentage = (obtainedMarks * 100.0) / totalMarks;

            // Additional safety check
            if (Double.isInfinite(percentage) || Double.isNaN(percentage)) {
                percentage = 0;
            }
        }

        // Calculate new average safely
        averagePercentage = ((averagePercentage * (totalAssessments - 1)) + percentage) / totalAssessments;

        // Update pass/fail counts
        if (obtainedMarks >= passMarks) {
            passedAssessments++;
        } else {
            failedAssessments++;
        }
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public Subject getSubject() { return subject; }
    public void setSubject(Subject subject) { this.subject = subject; }

    public double getAveragePercentage() { return averagePercentage; }
    public void setAveragePercentage(double averagePercentage) { this.averagePercentage = averagePercentage; }

    public int getTotalAssessments() { return totalAssessments; }
    public void setTotalAssessments(int totalAssessments) { this.totalAssessments = totalAssessments; }

    public int getPassedAssessments() { return passedAssessments; }
    public void setPassedAssessments(int passedAssessments) { this.passedAssessments = passedAssessments; }

    public int getFailedAssessments() { return failedAssessments; }
    public void setFailedAssessments(int failedAssessments) { this.failedAssessments = failedAssessments; }

    public double getImprovementPercentage() { return improvementPercentage; }
    public void setImprovementPercentage(double improvementPercentage) {
        this.improvementPercentage = improvementPercentage;
    }
}

package com.kops.sem_tracker.entyties;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "marks")
public class Marks {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String studentId;

    @ManyToOne
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @Column(nullable = false)
    private int obtainedMarks;

    @Column(nullable = false)
    private int totalMarks;

    @Column(nullable = false)
    private int passMarks;

    @Column(nullable = false)
    private String assessmentType; // QUIZ, EXAM, ASSIGNMENT, etc.

    @Column(nullable = false)
    private LocalDate assessmentDate;

    private String remarks;

    public Marks(){
        
    }

    public Marks(Long id, String studentId, Subject subject, int obtainedMarks, int totalMarks, int passMarks, String assessmentType, LocalDate assessmentDate, String remarks) {
        this.id = id;
        this.studentId = studentId;
        this.subject = subject;
        this.obtainedMarks = obtainedMarks;
        this.totalMarks = totalMarks;
        this.passMarks = passMarks;
        this.assessmentType = assessmentType;
        this.assessmentDate = assessmentDate;
        this.remarks = remarks;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public int getObtainedMarks() {
        return obtainedMarks;
    }

    public void setObtainedMarks(int obtainedMarks) {
        this.obtainedMarks = obtainedMarks;
    }

    public int getTotalMarks() {
        return totalMarks;
    }

    public void setTotalMarks(int totalMarks) {
        this.totalMarks = totalMarks;
    }

    public int getPassMarks() {
        return passMarks;
    }

    public void setPassMarks(int passMarks) {
        this.passMarks = passMarks;
    }

    public String getAssessmentType() {
        return assessmentType;
    }

    public void setAssessmentType(String assessmentType) {
        this.assessmentType = assessmentType;
    }

    public LocalDate getAssessmentDate() {
        return assessmentDate;
    }

    public void setAssessmentDate(LocalDate assessmentDate) {
        this.assessmentDate = assessmentDate;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    // Business methods
    public double calculatePercentage() {
        if (totalMarks <= 0) return 0;
        return (obtainedMarks * 100.0) / totalMarks;
    }

    public boolean isPassed() {
        return obtainedMarks >= passMarks;
    }

    public int getMarksNeededToPass() {
        return Math.max(0, passMarks - obtainedMarks);
    }

    public double getPercentageNeededToPass() {
        if (totalMarks <= 0) return 0;
        return (getMarksNeededToPass() * 100.0) / totalMarks;
    }
}
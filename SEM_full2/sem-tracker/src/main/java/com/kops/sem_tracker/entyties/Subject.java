package com.kops.sem_tracker.entyties;

import jakarta.persistence.*;

@Entity
@Table(name = "subjects")
public class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String subjectName;

    @Column(nullable = false)
    private String subjectCode;

    private int totalMarks;

    private int passMarks;

    private String description;

    // Constructors
    public Subject() {}

    public Subject(String subjectName, String subjectCode, int totalMarks, int passMarks) {
        this.subjectName = subjectName;
        this.subjectCode = subjectCode;
        this.totalMarks = totalMarks;
        this.passMarks = passMarks;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }

    public String getSubjectCode() { return subjectCode; }
    public void setSubjectCode(String subjectCode) { this.subjectCode = subjectCode; }

    public int getTotalMarks() { return totalMarks; }
    public void setTotalMarks(int totalMarks) { this.totalMarks = totalMarks; }

    public int getPassMarks() { return passMarks; }
    public void setPassMarks(int passMarks) { this.passMarks = passMarks; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
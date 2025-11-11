package com.kops.sem_tracker.entyties;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class SubjectAttendanceSettings {

    @Id
    private Long id;
    private String studentId;
    private String subjectName;
    private double totalScheduledHours;
    private double minPercentage;

    // Add more fields as needed
}

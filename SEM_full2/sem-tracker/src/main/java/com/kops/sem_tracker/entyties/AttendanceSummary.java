package com.kops.sem_tracker.entyties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;    // ← Add this import
import java.time.LocalTime;

@Entity
@Table(name = "attendance_summary")
public class AttendanceSummary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String studentId;

    @Column(nullable = false)
    private String moduleName;

    private double totalAttendedHours;

    private double totalScheduledHours;

    private double attendancePercentage;

    // Constructors
    public AttendanceSummary() {}

    public AttendanceSummary(String studentId, String moduleName) {
        this.studentId = studentId;
        this.moduleName = moduleName;
        this.totalAttendedHours = 0;
        this.totalScheduledHours = 0;
        this.attendancePercentage = 0;
    }

    // Method to update summary
    public void updateSummary(double attendedHours, double scheduledHours) {
        this.totalAttendedHours += attendedHours;
        this.totalScheduledHours += scheduledHours;

        if (totalScheduledHours > 0) {
            this.attendancePercentage = (totalAttendedHours / totalScheduledHours) * 100;
        } else {
            this.attendancePercentage = 0;
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getModuleName() { return moduleName; }
    public void setModuleName(String moduleName) { this.moduleName = moduleName; }

    public double getTotalAttendedHours() { return totalAttendedHours; }
    public void setTotalAttendedHours(double totalAttendedHours) {
        this.totalAttendedHours = totalAttendedHours;
    }

    public double getTotalScheduledHours() { return totalScheduledHours; }
    public void setTotalScheduledHours(double totalScheduledHours) {
        this.totalScheduledHours = totalScheduledHours;
    }

    public double getAttendancePercentage() { return attendancePercentage; }
    public void setAttendancePercentage(double attendancePercentage) {
        this.attendancePercentage = attendancePercentage;
    }
}

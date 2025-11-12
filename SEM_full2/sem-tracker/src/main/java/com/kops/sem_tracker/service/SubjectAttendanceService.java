package com.kops.sem_tracker.service;

import com.kops.sem_tracker.entyties.SubjectAttendanceSettings;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SubjectAttendanceService {

    // Update all subject attendance (you should implement actual logic here)
    public void updateAllSubjectAttendance(String studentId, String moduleName) {
        // TODO: implement logic to recalculate subject attendance for this student & module
    }

    // Get summary of attendance settings for a student
    public List<SubjectAttendanceSettings> getAttendanceSummary(String studentId) {
        // TODO: fetch from repository
        return List.of(); // return empty list for now
    }

    // Save or update attendance settings
    public SubjectAttendanceSettings saveSettings(SubjectAttendanceSettings settings) {
        // TODO: save using repository
        return settings;
    }

    // Get a specific subject's settings
    public Optional<SubjectAttendanceSettings> getSubjectSettings(String studentId, String subjectName) {
        // TODO: fetch from repository
        return Optional.empty();
    }
}

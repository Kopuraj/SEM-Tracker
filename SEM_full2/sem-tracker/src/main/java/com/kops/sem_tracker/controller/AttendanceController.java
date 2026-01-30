package com.kops.sem_tracker.controller;

import com.kops.sem_tracker.entyties.Attendance;
import com.kops.sem_tracker.entyties.SubjectAttendanceSettings;
import com.kops.sem_tracker.service.AttendanceService;
import com.kops.sem_tracker.service.SubjectAttendanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/attendance")
@CrossOrigin(origins = "*")
public class AttendanceController {

    private final AttendanceService attendanceService;
    private final SubjectAttendanceService subjectAttendanceService;

    public AttendanceController(AttendanceService attendanceService,
                                SubjectAttendanceService subjectAttendanceService) {
        this.attendanceService = attendanceService;
        this.subjectAttendanceService = subjectAttendanceService;
    }

    // ✅ Add a new attendance record
    @PostMapping
    public ResponseEntity<Attendance> create(@RequestBody Attendance attendance) {
        try {
            Attendance saved = attendanceService.save(attendance);
            // Update subject attendance settings
            subjectAttendanceService.updateAllSubjectAttendance(attendance.getStudentId(), attendance.getModuleName());
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ✅ Get all attendance records
    @GetMapping
    public ResponseEntity<List<Attendance>> getAll() {
        return ResponseEntity.ok(attendanceService.getAll());
    }

    // ✅ Get attendance by ID
    @GetMapping("/{id}")
    public ResponseEntity<Attendance> getById(@PathVariable Long id) {
        return attendanceService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ✅ Get attendance by student ID
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<Attendance>> getByStudentId(@PathVariable String studentId) {
        return ResponseEntity.ok(attendanceService.getByStudentId(studentId));
    }

    // ✅ Get attendance by student ID and module name
    @GetMapping("/student/{studentId}/module/{moduleName}")
    public ResponseEntity<List<Attendance>> getByStudentAndModule(
            @PathVariable String studentId,
            @PathVariable String moduleName) {
        return ResponseEntity.ok(attendanceService.getByStudentIdAndModule(studentId, moduleName));
    }

    // ✅ Update attendance record
    @PutMapping("/{id}")
    public ResponseEntity<Attendance> update(@PathVariable Long id, @RequestBody Attendance newData) {
        try {
            Attendance updated = attendanceService.update(id, newData);
            // Update subject attendance settings
            subjectAttendanceService.updateAllSubjectAttendance(updated.getStudentId(), updated.getModuleName());
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ✅ Delete attendance record
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            Optional<Attendance> attendance = attendanceService.getById(id);
            if (attendance.isPresent()) {
                String studentId = attendance.get().getStudentId();
                String moduleName = attendance.get().getModuleName();
                attendanceService.delete(id);
                // Update subject attendance settings
                subjectAttendanceService.updateAllSubjectAttendance(studentId, moduleName);
            }
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ✅ Get total attended hours for a student and module
    @GetMapping("/student/{studentId}/module/{moduleName}/total-hours")
    public ResponseEntity<Map<String, Object>> getTotalAttendedHours(
            @PathVariable String studentId,
            @PathVariable String moduleName) {
        try {
            double totalHours = attendanceService.getTotalAttendedHours(studentId, moduleName);
            return ResponseEntity.ok(Map.of("totalAttendedHours", totalHours));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ✅ Calculate attendance percentage
    @GetMapping("/student/{studentId}/module/{moduleName}/percentage")
    public ResponseEntity<Map<String, Object>> getAttendancePercentage(
            @PathVariable String studentId,
            @PathVariable String moduleName,
            @RequestParam double totalScheduledHours) {
        try {
            double percentage = attendanceService.calculateAttendancePercentage(
                    studentId, moduleName, totalScheduledHours);
            return ResponseEntity.ok(Map.of(
                    "attendancePercentage", percentage,
                    "studentId", studentId,
                    "moduleName", moduleName
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ✅ Calculate how many more hours needed to reach a minimum percentage
    @GetMapping("/student/{studentId}/module/{moduleName}/eligibility")
    public ResponseEntity<Map<String, Object>> getEligibilityGap(
            @PathVariable String studentId,
            @PathVariable String moduleName,
            @RequestParam double totalScheduledHours,
            @RequestParam double minPercentage) {
        try {
            double attended = attendanceService.getTotalAttendedHours(studentId, moduleName);
            double requiredHours = (minPercentage / 100.0) * totalScheduledHours;
            double moreNeeded = Math.max(0, requiredHours - attended);
            double currentPercentage = totalScheduledHours > 0 ? (attended / totalScheduledHours) * 100.0 : 0;
            return ResponseEntity.ok(Map.of(
                    "studentId", studentId,
                    "moduleName", moduleName,
                    "currentPercentage", currentPercentage,
                    "requiredPercentage", minPercentage,
                    "requiredHours", requiredHours,
                    "attendedHours", attended,
                    "moreHoursNeeded", moreNeeded
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ✅ Get attendance summary for a student
    @GetMapping("/student/{studentId}/summary")
    public ResponseEntity<Map<String, Object>> getAttendanceSummary(@PathVariable String studentId) {
        try {
            List<Attendance> allRecords = attendanceService.getByStudentId(studentId);

            double totalAttendedHours = allRecords.stream()
                    .mapToDouble(Attendance::getAttendedHours)
                    .sum();

            // This would need to be provided or calculated based on your scheduling system
            double totalScheduledHours = totalAttendedHours * 1.2; // Example calculation

            double overallPercentage = totalScheduledHours > 0 ?
                    (totalAttendedHours / totalScheduledHours) * 100 : 0;

            return ResponseEntity.ok(Map.of(
                    "studentId", studentId,
                    "totalAttendedHours", totalAttendedHours,
                    "totalScheduledHours", totalScheduledHours,
                    "overallAttendancePercentage", overallPercentage,
                    "totalRecords", allRecords.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ✅ SUBJECT-WISE ATTENDANCE ENDPOINTS

    // Get all subject attendance settings for a student
    @GetMapping("/student/{studentId}/subjects")
    public ResponseEntity<List<SubjectAttendanceSettings>> getStudentSubjectSettings(@PathVariable String studentId) {
        try {
            List<SubjectAttendanceSettings> settings = subjectAttendanceService.getAttendanceSummary(studentId);
            return ResponseEntity.ok(settings);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Create or update subject attendance settings
    @PostMapping("/student/{studentId}/subjects")
    public ResponseEntity<SubjectAttendanceSettings> saveSubjectSettings(
            @PathVariable String studentId,
            @RequestBody SubjectAttendanceSettings settings) {
        try {
            settings.setStudentId(studentId);
            SubjectAttendanceSettings saved = subjectAttendanceService.saveSettings(settings);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Get specific subject attendance settings
    @GetMapping("/student/{studentId}/subjects/{subjectName}")
    public ResponseEntity<SubjectAttendanceSettings> getSubjectSettings(
            @PathVariable String studentId,
            @PathVariable String subjectName) {
        try {
            Optional<SubjectAttendanceSettings> settings = subjectAttendanceService.getSubjectSettings(studentId, subjectName);
            return settings.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
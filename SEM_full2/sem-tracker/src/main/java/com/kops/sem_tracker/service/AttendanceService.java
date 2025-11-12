package com.kops.sem_tracker.service;

import com.kops.sem_tracker.entyties.Attendance;
import com.kops.sem_tracker.entyties.AttendanceSummary;
import com.kops.sem_tracker.repository.AttendanceRepository;
import com.kops.sem_tracker.repository.AttendanceSummaryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final AttendanceSummaryRepository summaryRepository;

    public AttendanceService(AttendanceRepository attendanceRepository,
                             AttendanceSummaryRepository summaryRepository) {
        this.attendanceRepository = attendanceRepository;
        this.summaryRepository = summaryRepository;
    }

    public Attendance save(Attendance attendance) {
        Attendance savedAttendance = attendanceRepository.save(attendance);
        updateAttendanceSummary(savedAttendance);
        return savedAttendance;
    }

    public List<Attendance> getAll() {
        return attendanceRepository.findAll();
    }

    public Optional<Attendance> getById(Long id) {
        return attendanceRepository.findById(id);
    }

    public List<Attendance> getByStudentId(String studentId) {
        return attendanceRepository.findByStudentId(studentId);
    }

    public List<Attendance> getByStudentIdAndModule(String studentId, String moduleName) {
        return attendanceRepository.findByStudentIdAndModuleName(studentId, moduleName);
    }

    public Attendance update(Long id, Attendance newData) {
        return attendanceRepository.findById(id)
                .map(existing -> {
                    // Store old values for summary update
                    double oldAttendedHours = existing.getAttendedHours();
                    String oldModuleName = existing.getModuleName();

                    existing.setModuleName(newData.getModuleName());
                    existing.setAttendanceDate(newData.getAttendanceDate());
                    existing.setStartTime(newData.getStartTime());
                    existing.setEndTime(newData.getEndTime());
                    existing.setStudentId(newData.getStudentId());

                    Attendance updated = attendanceRepository.save(existing);

                    // Update summary for both old and new module if module changed
                    if (!oldModuleName.equals(newData.getModuleName())) {
                        revertSummaryForModule(existing.getStudentId(), oldModuleName, oldAttendedHours);
                    }
                    updateAttendanceSummary(updated);

                    return updated;
                })
                .orElseGet(() -> {
                    newData.setId(id);
                    return attendanceRepository.save(newData);
                });
    }

    public void delete(Long id) {
        attendanceRepository.findById(id).ifPresent(attendance -> {
            // Revert summary before deletion
            revertSummaryForModule(attendance.getStudentId(),
                    attendance.getModuleName(),
                    attendance.getAttendedHours());
            attendanceRepository.deleteById(id);
        });
    }

    public double getTotalAttendedHours(String studentId, String moduleName) {
        Double total = attendanceRepository.getTotalAttendedHoursByStudentAndModule(studentId, moduleName);
        return total != null ? total : 0.0;
    }

    public double calculateAttendancePercentage(String studentId, String moduleName, double totalScheduledHours) {
        if (totalScheduledHours <= 0) return 0.0;

        double totalAttended = getTotalAttendedHours(studentId, moduleName);
        return (totalAttended / totalScheduledHours) * 100;
    }

    @Transactional
    private void updateAttendanceSummary(Attendance attendance) {
        String studentId = attendance.getStudentId();
        String moduleName = attendance.getModuleName();
        double attendedHours = attendance.getAttendedHours();

        // For simplicity, we'll assume each attendance entry represents a scheduled session
        double scheduledHours = attendedHours; // In real scenario, this might be different

        Optional<AttendanceSummary> existingSummary =
                summaryRepository.findByStudentIdAndModuleName(studentId, moduleName);

        if (existingSummary.isPresent()) {
            AttendanceSummary summary = existingSummary.get();
            summary.updateSummary(attendedHours, scheduledHours);
            summaryRepository.save(summary);
        } else {
            AttendanceSummary newSummary = new AttendanceSummary(studentId, moduleName);
            newSummary.updateSummary(attendedHours, scheduledHours);
            summaryRepository.save(newSummary);
        }
    }

    @Transactional
    private void revertSummaryForModule(String studentId, String moduleName, double attendedHours) {
        Optional<AttendanceSummary> summaryOpt =
                summaryRepository.findByStudentIdAndModuleName(studentId, moduleName);

        if (summaryOpt.isPresent()) {
            AttendanceSummary summary = summaryOpt.get();
            // Subtract the hours from the summary
            summary.setTotalAttendedHours(Math.max(0, summary.getTotalAttendedHours() - attendedHours));
            summary.setTotalScheduledHours(Math.max(0, summary.getTotalScheduledHours() - attendedHours));

            if (summary.getTotalScheduledHours() > 0) {
                summary.setAttendancePercentage(
                        (summary.getTotalAttendedHours() / summary.getTotalScheduledHours()) * 100
                );
            } else {
                summary.setAttendancePercentage(0);
            }

            summaryRepository.save(summary);
        }
    }
}

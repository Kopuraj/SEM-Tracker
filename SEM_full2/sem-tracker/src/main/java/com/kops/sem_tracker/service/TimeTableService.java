package com.kops.sem_tracker.service;

import com.kops.sem_tracker.entyties.TimeTable;
import com.kops.sem_tracker.repository.TimeTableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TimeTableService {

    @Autowired
    private TimeTableRepository timetableRepository;

    public List<TimeTable> getAll() {
        return timetableRepository.findAll();
    }

    public List<TimeTable> getAllForUser(String username) {
        return timetableRepository.findByUsername(username);
    }

    public Optional<TimeTable> getById(Long id) {
        return timetableRepository.findById(id);
    }

    public TimeTable save(TimeTable timeTable) {
        // Ensure title is set before saving
        if (timeTable.getTitle() == null) {
            timeTable.setTitle(timeTable.getSubject() != null ? timeTable.getSubject() : "Untitled");
        }

        // For special schedules, ensure day is set based on specialDate
        if (timeTable.isSpecialSchedule() && timeTable.getSpecialDate() != null && timeTable.getDay() == null) {
            try {
                LocalDate date = LocalDate.parse(timeTable.getSpecialDate());
                timeTable.setDay(date.getDayOfWeek().toString());
            } catch (Exception e) {
                timeTable.setDay("MONDAY"); // Default fallback
            }
        }

        // For regular schedules, ensure specialDate is null
        if (!timeTable.isSpecialSchedule()) {
            timeTable.setSpecialDate(null);
        }

        return timetableRepository.save(timeTable);
    }

    public TimeTable update(Long id, TimeTable newData) {
        return timetableRepository.findById(id)
                .map(existing -> {
                    existing.setSubject(newData.getSubject());
                    existing.setDay(newData.getDay());
                    existing.setStartTime(newData.getStartTime());
                    existing.setEndTime(newData.getEndTime());
                    existing.setLocation(newData.getLocation());
                    existing.setLecturer(newData.getLecturer());
                    existing.setNotificationPreference(newData.getNotificationPreference());
                    existing.setTitle(newData.getTitle() != null ? newData.getTitle() : newData.getSubject());
                    existing.setDescription(newData.getDescription());
                    existing.setSpecialSchedule(newData.isSpecialSchedule());
                    existing.setSpecialDate(newData.getSpecialDate());

                    // Ensure data consistency
                    if (!existing.isSpecialSchedule()) {
                        existing.setSpecialDate(null);
                    }

                    return timetableRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Timetable not found with id: " + id));
    }

    public void delete(Long id) {
        timetableRepository.deleteById(id);
    }

    // Get ALL schedules by day (includes both regular and special - for backward compatibility)
    public List<TimeTable> getByDay(String day) {
        return timetableRepository.findByDay(day);
    }

    // Get only REGULAR schedules by day (excludes special schedules)
    public List<TimeTable> getRegularByDay(String day) {
        return timetableRepository.findRegularByDay(day);
    }

    public List<TimeTable> getRegularByDayForUser(String username, String day) {
        return timetableRepository.findByUsernameAndDay(username, day);
    }

    // Get special schedules for a specific date
    public List<TimeTable> getBySpecialDate(String date) {
        return timetableRepository.findBySpecialDate(date);
    }

    // Get combined schedules for a specific date (regular + special for that date)
    public List<TimeTable> getByDayAndDate(String day, String date) {
        // Get regular schedules for this day of week
        List<TimeTable> regularSchedules = getRegularByDay(day);

        // Get special schedules for this specific date
        List<TimeTable> specialSchedules = getBySpecialDate(date);

        // Combine both lists
        List<TimeTable> combinedSchedules = new ArrayList<>();
        combinedSchedules.addAll(regularSchedules);
        combinedSchedules.addAll(specialSchedules);

        return combinedSchedules;
    }

    // Get today's schedules (regular for today's day + special for today's date)
    public List<TimeTable> getTodaySchedules() {
        LocalDate today = LocalDate.now();
        String dayOfWeek = today.getDayOfWeek().toString();
        String dateString = today.toString();

        return getByDayAndDate(dayOfWeek, dateString);
    }

    // Get upcoming events between time range (for notifications) - considers special dates
    public List<TimeTable> getEventsBetween(LocalTime start, LocalTime end) {
        LocalDate today = LocalDate.now();
        return timetableRepository.findUpcomingEvents(start, end, today.toString());
    }

    // Get all special schedules
    public List<TimeTable> getSpecialSchedules() {
        return timetableRepository.findByIsSpecialScheduleTrue();
    }

    // Get active schedules for a specific date (both regular and special that are active on that date)
    public List<TimeTable> getActiveSchedulesForDate(String date) {
        try {
            LocalDate localDate = LocalDate.parse(date);
            String dayOfWeek = localDate.getDayOfWeek().toString();

            // Get regular schedules for this day of week
            List<TimeTable> regularSchedules = getRegularByDay(dayOfWeek);

            // Get special schedules for this specific date
            List<TimeTable> specialSchedules = getBySpecialDate(date);

            // Combine both lists
            List<TimeTable> activeSchedules = new ArrayList<>();
            activeSchedules.addAll(regularSchedules);
            activeSchedules.addAll(specialSchedules);

            return activeSchedules;
        } catch (Exception e) {
            throw new RuntimeException("Invalid date format: " + date, e);
        }
    }

    // Search methods
    public List<TimeTable> searchByLocation(String location) {
        return timetableRepository.findByLocationContainingIgnoreCase(location);
    }

    public List<TimeTable> searchByLecturer(String lecturer) {
        return timetableRepository.findByLecturerContainingIgnoreCase(lecturer);
    }

    public List<TimeTable> searchBySubject(String subject) {
        return timetableRepository.findBySubjectContainingIgnoreCase(subject);
    }

    public List<TimeTable> getByNotificationPreference(String preference) {
        return timetableRepository.findByNotificationPreference(preference);
    }

    // Check if a schedule with the same details already exists (for validation)
    public boolean scheduleExists(TimeTable timeTable) {
        if (timeTable.isSpecialSchedule()) {
            // For special schedules, check by subject, date, and time
            return timetableRepository.findAll().stream()
                    .anyMatch(existing ->
                            existing.isSpecialSchedule() &&
                                    existing.getSpecialDate() != null &&
                                    existing.getSpecialDate().equals(timeTable.getSpecialDate()) &&
                                    existing.getSubject().equalsIgnoreCase(timeTable.getSubject()) &&
                                    existing.getStartTime().equals(timeTable.getStartTime()) &&
                                    existing.getEndTime().equals(timeTable.getEndTime()) &&
                                    (existing.getId() == null || !existing.getId().equals(timeTable.getId()))
                    );
        } else {
            // For regular schedules, check by subject, day, and time
            return timetableRepository.findAll().stream()
                    .anyMatch(existing ->
                            !existing.isSpecialSchedule() &&
                                    existing.getDay().equalsIgnoreCase(timeTable.getDay()) &&
                                    existing.getSubject().equalsIgnoreCase(timeTable.getSubject()) &&
                                    existing.getStartTime().equals(timeTable.getStartTime()) &&
                                    existing.getEndTime().equals(timeTable.getEndTime()) &&
                                    (existing.getId() == null || !existing.getId().equals(timeTable.getId()))
                    );
        }
    }

    // Get schedules for a date range
    public List<TimeTable> getSchedulesForDateRange(LocalDate startDate, LocalDate endDate) {
        List<TimeTable> allSchedules = new ArrayList<>();

        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            String dateString = currentDate.toString();
            List<TimeTable> dateSchedules = getActiveSchedulesForDate(dateString);

            // Add date context for display
            dateSchedules.forEach(schedule -> {
                if (!schedule.isSpecialSchedule()) {
                    schedule.setDisplayDate(dateString);
                }
            });

            allSchedules.addAll(dateSchedules);
            currentDate = currentDate.plusDays(1);
        }

        return allSchedules;
    }

    public boolean scheduleExistsExcludingId(TimeTable updatedEntity, Long id) {
        return false;
    }

    // ========== USER-SPECIFIC SCHEDULE METHODS ==========

    /**
     * Get today's schedules for a specific user (both regular and special)
     */
    public List<TimeTable> getTodaySchedulesForUser(String username) {
        LocalDate today = LocalDate.now();
        String dayOfWeek = today.getDayOfWeek().toString();
        String dateString = today.toString();

        List<TimeTable> schedules = new ArrayList<>();

        // Get user's regular schedules for today's day of week
        List<TimeTable> regularSchedules = timetableRepository.findByUsernameAndDay(username, dayOfWeek)
                .stream()
                .filter(s -> !s.isSpecialSchedule())
                .toList();
        schedules.addAll(regularSchedules);

        // Get user's special schedules for today's exact date
        List<TimeTable> specialSchedules = timetableRepository.findBySpecialDate(dateString)
                .stream()
                .filter(s -> s.getUsername() != null && s.getUsername().equals(username))
                .toList();
        schedules.addAll(specialSchedules);

        return schedules;
    }

    /**
     * Get schedules for a specific user and day
     */
    public List<TimeTable> getSchedulesForUserAndDay(String username, String day) {
        return timetableRepository.findByUsernameAndDay(username, day)
                .stream()
                .filter(s -> !s.isSpecialSchedule())
                .toList();
    }

    /**
     * Get upcoming schedules for a user (from today onwards)
     */
    public List<TimeTable> getUpcomingSchedulesForUser(String username, int days) {
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(days);

        List<TimeTable> upcomingSchedules = new ArrayList<>();

        LocalDate currentDate = today;
        while (!currentDate.isAfter(endDate)) {
            String dayOfWeek = currentDate.getDayOfWeek().toString();
            String dateString = currentDate.toString();

            // Get regular schedules for this day
            List<TimeTable> regularSchedules = timetableRepository.findByUsernameAndDay(username, dayOfWeek)
                    .stream()
                    .filter(s -> !s.isSpecialSchedule())
                    .toList();

            // Add display date for context
            for (TimeTable schedule : regularSchedules) {
                schedule.setDisplayDate(dateString);
            }
            upcomingSchedules.addAll(regularSchedules);

            // Get special schedules for this date
            List<TimeTable> specialSchedules = timetableRepository.findBySpecialDate(dateString)
                    .stream()
                    .filter(s -> s.getUsername() != null && s.getUsername().equals(username))
                    .toList();
            upcomingSchedules.addAll(specialSchedules);

            currentDate = currentDate.plusDays(1);
        }

        return upcomingSchedules;
    }

    /**
     * Get all special schedules for a user (future only)
     */
    public List<TimeTable> getFutureSpecialSchedulesForUser(String username) {
        LocalDate today = LocalDate.now();
        String todayString = today.toString();

        return timetableRepository.findByIsSpecialScheduleTrue()
                .stream()
                .filter(s -> s.getUsername() != null && s.getUsername().equals(username))
                .filter(s -> s.getSpecialDate() != null && s.getSpecialDate().compareTo(todayString) >= 0)
                .toList();
    }

    /**
     * Get week schedule for a user (next 7 days)
     */
    public List<TimeTable> getWeekScheduleForUser(String username) {
        return getUpcomingSchedulesForUser(username, 7);
    }

    /**
     * Check if user has any class on a specific date
     */
    public boolean hasClassOnDate(String username, String date) {
        try {
            LocalDate localDate = LocalDate.parse(date);
            String dayOfWeek = localDate.getDayOfWeek().toString();

            // Check regular schedules
            List<TimeTable> regularSchedules = timetableRepository.findByUsernameAndDay(username, dayOfWeek);
            if (!regularSchedules.isEmpty()) {
                return true;
            }

            // Check special schedules
            List<TimeTable> specialSchedules = timetableRepository.findBySpecialDate(date)
                    .stream()
                    .filter(s -> s.getUsername() != null && s.getUsername().equals(username))
                    .toList();
            
            return !specialSchedules.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }
}
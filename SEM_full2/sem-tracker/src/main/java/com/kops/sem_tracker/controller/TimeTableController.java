package com.kops.sem_tracker.controller;

import com.kops.sem_tracker.entyties.TimeTable;
import com.kops.sem_tracker.service.TimeTableService;
import com.kops.sem_tracker.service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.*;

@RestController
@RequestMapping("/api/timetable")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class TimeTableController {

    private final TimeTableService timetableService;
    private final NotificationService notificationService;

    public TimeTableController(TimeTableService timetableService, NotificationService notificationService) {
        this.timetableService = timetableService;
        this.notificationService = notificationService;
    }

    /** ✅ CREATE TIMETABLE ENTRY **/
    @PostMapping
    public ResponseEntity<?> create(@RequestBody TimeTable timeTable) {
        try {
            System.out.println("🔍 CREATE Request received: " + timeTable.toString());

            // Validate request
            String validationError = validateTimeTable(timeTable);
            if (validationError != null) {
                System.out.println("❌ Validation failed: " + validationError);
                return ResponseEntity.badRequest().body(Map.of("error", validationError));
            }

            // Handle special schedule day calculation
            if (timeTable.isSpecialSchedule() && timeTable.getSpecialDate() != null) {
                try {
                    LocalDate specialDate = LocalDate.parse(timeTable.getSpecialDate());
                    String dayOfWeek = specialDate.getDayOfWeek().toString();
                    timeTable.setDay(dayOfWeek);
                    System.out.println("📅 Special schedule day calculated: " + dayOfWeek);
                } catch (Exception e) {
                    System.out.println("❌ Error parsing special date: " + e.getMessage());
                    return ResponseEntity.badRequest().body(Map.of("error", "Invalid special date format"));
                }
            }

            // Ensure username is properly set - Critical Fix!
            if (timeTable.getUsername() == null || timeTable.getUsername().trim().isEmpty()) {
                System.out.println("⚠️ Username is missing from request");
                return ResponseEntity.badRequest().body(Map.of("error", "Username is required"));
            }

            // Prevent duplicate schedules
            if (timetableService.scheduleExists(timeTable)) {
                System.out.println("❌ Duplicate schedule detected");
                return ResponseEntity.badRequest().body(Map.of("error", "Duplicate schedule detected"));
            }

            System.out.println("💾 Saving timetable entry...");
            TimeTable saved = timetableService.save(timeTable);

            if (saved == null || saved.getId() == null) {
                System.out.println("❌ Failed to save - returned null or no ID");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("error", "Failed to save timetable entry"));
            }

            System.out.println("✅ Successfully saved with ID: " + saved.getId());

            // Schedule notifications if applicable
            if (saved.getNotificationPreference() != null && !"NONE".equals(saved.getNotificationPreference())) {
                try {
                    notificationService.scheduleNotification(saved);
                    System.out.println("🔔 Notification scheduled");
                } catch (Exception e) {
                    System.out.println("⚠️ Failed to schedule notification: " + e.getMessage());
                }
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(saved);

        } catch (Exception e) {
            System.out.println("❌ CREATE Error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to create timetable: " + e.getMessage()));
        }
    }

    /** ✅ GET ALL ENTRIES **/
    @GetMapping
    public ResponseEntity<?> getAll(@RequestParam(required = false) String username) {
        try {
            System.out.println("🔍 GET ALL Request - Username: " + username);
            if (username != null && !username.isBlank()) {
                List<TimeTable> userEntries = timetableService.getAllForUser(username);
                System.out.println("📋 Found " + userEntries.size() + " entries for user: " + username);
                return ResponseEntity.ok(userEntries);
            }
            List<TimeTable> allEntries = timetableService.getAll();
            System.out.println("📋 Found " + allEntries.size() + " total entries");
            return ResponseEntity.ok(allEntries);
        } catch (Exception e) {
            System.out.println("❌ GET ALL Error: " + e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to fetch timetables: " + e.getMessage()));
        }
    }

    /** ✅ GET ENTRY BY DAY **/
    @GetMapping("/day/{day}")
    public ResponseEntity<?> getByDay(@PathVariable String day, @RequestParam(required = false) String username) {
        try {
            String normalizedDay = day.trim().toUpperCase();
            System.out.println("🔍 GET BY DAY Request - Day: " + normalizedDay + ", Username: " + username);

            if (username != null && !username.isBlank()) {
                List<TimeTable> userDayEntries = timetableService.getRegularByDayForUser(username, normalizedDay);
                System.out.println("📋 Found " + userDayEntries.size() + " entries for " + normalizedDay);
                return ResponseEntity.ok(userDayEntries);
            }
            List<TimeTable> dayEntries = timetableService.getRegularByDay(normalizedDay);
            System.out.println("📋 Found " + dayEntries.size() + " entries for " + normalizedDay);
            return ResponseEntity.ok(dayEntries);
        } catch (Exception e) {
            System.out.println("❌ GET BY DAY Error: " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /** ✅ GET SPECIAL SCHEDULES **/
    @GetMapping("/special")
    public ResponseEntity<?> getSpecial(@RequestParam(required = false) String username) {
        try {
            System.out.println("🔍 GET SPECIAL Request - Username: " + username);
            var all = timetableService.getSpecialSchedules();
            if (username != null && !username.isBlank()) {
                var userSpecial = all.stream().filter(t -> username.equals(t.getUsername())).toList();
                System.out.println("📋 Found " + userSpecial.size() + " special schedules for user");
                return ResponseEntity.ok(userSpecial);
            }
            System.out.println("📋 Found " + all.size() + " special schedules total");
            return ResponseEntity.ok(all);
        } catch (Exception e) {
            System.out.println("❌ GET SPECIAL Error: " + e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    /** ✅ GET ENTRY BY DATE **/
    @GetMapping("/date/{date}")
    public ResponseEntity<?> getByDate(@PathVariable String date) {
        try {
            System.out.println("🔍 GET BY DATE Request - Date: " + date);
            LocalDate localDate = LocalDate.parse(date);
            var entries = timetableService.getByDayAndDate(localDate.getDayOfWeek().toString(), localDate.toString());
            System.out.println("📋 Found " + entries.size() + " entries for date");
            return ResponseEntity.ok(entries);
        } catch (DateTimeParseException e) {
            System.out.println("❌ Invalid date format: " + date);
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid date format (use YYYY-MM-DD)"));
        }
    }

    /** ✅ GET TODAY'S TIMETABLE **/
    @GetMapping("/today")
    public ResponseEntity<?> getToday() {
        try {
            System.out.println("🔍 GET TODAY Request");
            var todayEntries = timetableService.getTodaySchedules();
            System.out.println("📋 Found " + todayEntries.size() + " entries for today");
            return ResponseEntity.ok(todayEntries);
        } catch (Exception e) {
            System.out.println("❌ GET TODAY Error: " + e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to fetch today's timetable"));
        }
    }

    /** ✅ GET ENTRY BY ID **/
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        System.out.println("🔍 GET BY ID Request - ID: " + id);
        return timetableService.getById(id)
                .<ResponseEntity<?>>map(entry -> {
                    System.out.println("✅ Found entry with ID: " + id);
                    return ResponseEntity.ok(entry);
                })
                .orElseGet(() -> {
                    System.out.println("❌ No entry found with ID: " + id);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(Map.of("error", "No timetable found with id: " + id));
                });
    }

    /** ✅ UPDATE ENTRY **/
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody TimeTable timeTable) {
        try {
            System.out.println("🔍 UPDATE Request - ID: " + id + ", Data: " + timeTable.toString());

            Optional<TimeTable> existing = timetableService.getById(id);
            if (existing.isEmpty()) {
                System.out.println("❌ Entry not found for update: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Timetable entry not found with id: " + id));
            }

            // Validate the updated data
            String validationError = validateTimeTable(timeTable);
            if (validationError != null) {
                System.out.println("❌ Update validation failed: " + validationError);
                return ResponseEntity.badRequest().body(Map.of("error", validationError));
            }

            // Preserve username from existing entry if not provided
            if (timeTable.getUsername() == null || timeTable.getUsername().trim().isEmpty()) {
                timeTable.setUsername(existing.get().getUsername());
                System.out.println("📝 Preserved username: " + existing.get().getUsername());
            }

            // Handle special schedule day calculation
            if (timeTable.isSpecialSchedule() && timeTable.getSpecialDate() != null) {
                try {
                    LocalDate specialDate = LocalDate.parse(timeTable.getSpecialDate());
                    String dayOfWeek = specialDate.getDayOfWeek().toString();
                    timeTable.setDay(dayOfWeek);
                    System.out.println("📅 Updated special schedule day: " + dayOfWeek);
                } catch (Exception e) {
                    System.out.println("❌ Error parsing updated special date: " + e.getMessage());
                    return ResponseEntity.badRequest().body(Map.of("error", "Invalid special date format"));
                }
            }

            // Cancel existing notifications
            try {
                notificationService.cancelNotification(existing.get());
                System.out.println("🔕 Cancelled existing notification");
            } catch (Exception e) {
                System.out.println("⚠️ Failed to cancel notification: " + e.getMessage());
            }

            timeTable.setId(id);

            // Check for duplicates excluding current entry
            if (timetableService.scheduleExistsExcludingId(timeTable, id)) {
                System.out.println("❌ Duplicate schedule detected during update");
                return ResponseEntity.badRequest().body(Map.of("error", "Another schedule with same details exists"));
            }

            System.out.println("💾 Updating timetable entry...");
            TimeTable updated = timetableService.update(id, timeTable);

            if (updated == null) {
                System.out.println("❌ Update failed - returned null");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("error", "Failed to update timetable entry"));
            }

            System.out.println("✅ Successfully updated entry with ID: " + id);

            // Schedule new notifications if applicable
            if (updated.getNotificationPreference() != null && !"NONE".equals(updated.getNotificationPreference())) {
                try {
                    notificationService.scheduleNotification(updated);
                    System.out.println("🔔 New notification scheduled");
                } catch (Exception e) {
                    System.out.println("⚠️ Failed to schedule new notification: " + e.getMessage());
                }
            }

            return ResponseEntity.ok(updated);

        } catch (Exception e) {
            System.out.println("❌ UPDATE Error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to update timetable: " + e.getMessage()));
        }
    }

    /** ✅ DELETE ENTRY **/
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            System.out.println("🔍 DELETE Request - ID: " + id);

            Optional<TimeTable> existing = timetableService.getById(id);
            if (existing.isEmpty()) {
                System.out.println("❌ Entry not found for deletion: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Timetable entry not found with id: " + id));
            }

            // Cancel notifications first
            try {
                notificationService.cancelNotification(existing.get());
                System.out.println("🔕 Cancelled notification for deleted entry");
            } catch (Exception e) {
                System.out.println("⚠️ Failed to cancel notification: " + e.getMessage());
            }

            timetableService.delete(id);
            System.out.println("✅ Successfully deleted entry with ID: " + id);

            return ResponseEntity.ok(Map.of("message", "Timetable entry deleted", "id", id));
        } catch (Exception e) {
            System.out.println("❌ DELETE Error: " + e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to delete timetable: " + e.getMessage()));
        }
    }

    /** ✅ UPCOMING EVENTS **/
    @GetMapping("/upcoming")
    public ResponseEntity<?> getUpcomingEvents() {
        try {
            System.out.println("🔍 GET UPCOMING Request");
            LocalTime now = LocalTime.now();
            var upcoming = timetableService.getEventsBetween(now, now.plusMinutes(30));
            System.out.println("📋 Found " + upcoming.size() + " upcoming events");
            return ResponseEntity.ok(upcoming);
        } catch (Exception e) {
            System.out.println("❌ GET UPCOMING Error: " + e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to fetch upcoming events"));
        }
    }

    /** ✅ SEARCH **/
    @GetMapping("/search/location/{location}")
    public ResponseEntity<?> searchByLocation(@PathVariable String location) {
        System.out.println("🔍 SEARCH BY LOCATION: " + location);
        return ResponseEntity.ok(timetableService.searchByLocation(location));
    }

    @GetMapping("/search/lecturer/{lecturer}")
    public ResponseEntity<?> searchByLecturer(@PathVariable String lecturer) {
        System.out.println("🔍 SEARCH BY LECTURER: " + lecturer);
        return ResponseEntity.ok(timetableService.searchByLecturer(lecturer));
    }

    @GetMapping("/search/subject/{subject}")
    public ResponseEntity<?> searchBySubject(@PathVariable String subject) {
        System.out.println("🔍 SEARCH BY SUBJECT: " + subject);
        return ResponseEntity.ok(timetableService.searchBySubject(subject));
    }

    /** ✅ HEALTH CHECK **/
    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        return ResponseEntity.ok(Map.of(
                "status", "OK",
                "timestamp", LocalTime.now().toString(),
                "service", "Timetable Service"
        ));
    }

    /** ✅ HELPER: Validate TimeTable - Enhanced **/
    private String validateTimeTable(TimeTable timeTable) {
        if (timeTable.getSubject() == null || timeTable.getSubject().trim().isEmpty()) {
            return "Subject is required";
        }

        if (timeTable.isSpecialSchedule()) {
            if (timeTable.getSpecialDate() == null || timeTable.getSpecialDate().isBlank()) {
                return "Special date is required for special schedules";
            }
            // Validate date format
            try {
                LocalDate.parse(timeTable.getSpecialDate());
            } catch (Exception e) {
                return "Invalid special date format (use YYYY-MM-DD)";
            }
        } else {
            if (timeTable.getDay() == null || timeTable.getDay().isBlank()) {
                return "Day is required for regular schedules";
            }
        }

        if (timeTable.getStartTime() == null) {
            return "Start time is required";
        }

        if (timeTable.getEndTime() == null) {
            return "End time is required";
        }

        // Validate time format and logic - FIXED THIS SECTION
        try {
            // Convert LocalTime to String for parsing (if needed)
            String startTimeStr = timeTable.getStartTime().toString();
            String endTimeStr = timeTable.getEndTime().toString();

            LocalTime start = LocalTime.parse(startTimeStr);
            LocalTime end = LocalTime.parse(endTimeStr);

            if (start.isAfter(end) || start.equals(end)) {
                return "End time must be after start time";
            }
        } catch (Exception e) {
            return "Invalid time format (use HH:mm:ss or HH:mm)";
        }

        // Validate username
        if (timeTable.getUsername() == null || timeTable.getUsername().trim().isEmpty()) {
            return "Username is required";
        }

        return null;
    }
}
package com.kops.sem_tracker.entyties;

import jakarta.persistence.*;
import java.time.LocalTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "timetables")
public class TimeTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String subject;

    private String location;

    private String lecturer;

    @Column(nullable = false)
    private String day;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Column(name = "is_weekly", nullable = false)
    private boolean isWeekly = true;

    @Column(name = "notification_preference")
    private String notificationPreference = "NONE";

    @Column(name = "username")
    private String username;

    @Column(name = "device_token")
    private String deviceToken;

    @Column(nullable = false)
    private String title;

    private String description;

    // Special schedule fields
    @Column(name = "is_special_schedule", nullable = false)
    private boolean isSpecialSchedule = false;

    @Column(name = "special_date")
    private String specialDate; // For special schedules (YYYY-MM-DD format)

    // Transient field for display purposes (not persisted in database)
    @Transient
    private String displayDate;

    // Constructors
    public TimeTable() {}

    public TimeTable(String subject, String day, LocalTime startTime, LocalTime endTime, String location, String lecturer) {
        this.subject = subject;
        this.day = day != null ? day.toUpperCase() : null;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.lecturer = lecturer;
        this.title = subject;
        this.isSpecialSchedule = false;
        this.specialDate = null;
        this.isWeekly = true;
        this.notificationPreference = "NONE";
    }

    // Constructor for special schedules
    public TimeTable(String subject, String specialDate, LocalTime startTime, LocalTime endTime,
                     String location, String lecturer, String description) {
        this.subject = subject;
        this.setSpecialDate(specialDate); // This will auto-set the day
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.lecturer = lecturer;
        this.description = description;
        this.isSpecialSchedule = true;
        this.title = subject;
        this.isWeekly = false;
        this.notificationPreference = "NONE";
    }

    // Add this method to ensure data consistency before saving
    @PrePersist
    @PreUpdate
    private void validateAndPrepareData() {
        // Ensure title is never null
        if (this.title == null) {
            this.title = this.subject != null ? this.subject : "Untitled";
        }

        // For special schedules, ensure specialDate is set and day is derived from it
        if (this.isSpecialSchedule) {
            if (this.specialDate == null || this.specialDate.trim().isEmpty()) {
                throw new IllegalStateException("Special date is required for special schedules");
            }

            // Auto-set day based on special date
            try {
                LocalDate date = LocalDate.parse(this.specialDate);
                this.day = date.getDayOfWeek().toString();
            } catch (Exception e) {
                throw new IllegalStateException("Invalid special date format: " + this.specialDate + ". Expected YYYY-MM-DD");
            }
        } else {
            // For regular schedules, ensure specialDate is null and day is set
            this.specialDate = null;
            if (this.day == null || this.day.trim().isEmpty()) {
                throw new IllegalStateException("Day is required for regular schedules");
            }
            this.day = this.day.toUpperCase(); // Normalize day to uppercase
        }

        // Validate time consistency
        if (this.startTime != null && this.endTime != null && !this.endTime.isAfter(this.startTime)) {
            throw new IllegalStateException("End time must be after start time");
        }

        // Ensure boolean fields have proper defaults
        if (!this.isSpecialSchedule) {
            this.isSpecialSchedule = false;
        }
        if (!this.isWeekly) {
            this.isWeekly = true;
        }
        if (this.notificationPreference == null) {
            this.notificationPreference = "NONE";
        }
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
        // Auto-update title if it was based on subject
        if (this.title == null || this.title.equals(this.subject)) {
            this.title = subject;
        }
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLecturer() {
        return lecturer;
    }

    public void setLecturer(String lecturer) {
        this.lecturer = lecturer;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        // Only allow setting day for regular schedules
        if (!this.isSpecialSchedule) {
            this.day = day != null ? day.toUpperCase() : null;
        }
        // For special schedules, day is derived from specialDate and cannot be manually set
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public boolean isWeekly() {
        return isWeekly;
    }

    public void setWeekly(boolean weekly) {
        isWeekly = weekly;
    }

    public String getNotificationPreference() {
        return notificationPreference;
    }

    public void setNotificationPreference(String notificationPreference) {
        this.notificationPreference = notificationPreference != null ? notificationPreference : "NONE";
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isSpecialSchedule() {
        return isSpecialSchedule;
    }

    public void setSpecialSchedule(boolean specialSchedule) {
        boolean wasSpecial = this.isSpecialSchedule;
        this.isSpecialSchedule = specialSchedule;

        if (wasSpecial && !specialSchedule) {
            // Changing from special to regular: clear special date
            this.specialDate = null;
        } else if (!wasSpecial && specialSchedule) {
            // Changing from regular to special: clear day (it will be set from specialDate)
            this.day = null;
        }
    }

    public String getSpecialDate() {
        return specialDate;
    }

    public void setSpecialDate(String specialDate) {
        if (specialDate != null && !specialDate.trim().isEmpty()) {
            // Validate date format
            try {
                LocalDate.parse(specialDate);
                this.specialDate = specialDate;

                // Auto-set day based on special date for special schedules
                if (this.isSpecialSchedule) {
                    LocalDate date = LocalDate.parse(specialDate);
                    this.day = date.getDayOfWeek().toString();
                }
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid date format: " + specialDate + ". Expected YYYY-MM-DD");
            }
        } else {
            this.specialDate = null;
        }
    }

    public String getDisplayDate() {
        return displayDate;
    }

    public void setDisplayDate(String displayDate) {
        this.displayDate = displayDate;
    }

    // Helper method to check if this schedule is active on a specific date
    public boolean isActiveOnDate(String date) {
        if (date == null) return false;

        if (this.isSpecialSchedule) {
            // For special schedules, only active on the exact date
            return date.equals(this.specialDate);
        } else {
            // For regular schedules, check if the day matches
            try {
                LocalDate localDate = LocalDate.parse(date);
                String dayOfWeek = localDate.getDayOfWeek().toString();
                return dayOfWeek.equals(this.day);
            } catch (Exception e) {
                return false;
            }
        }
    }

    // Helper method to check if this schedule is active today
    public boolean isActiveToday() {
        String today = LocalDate.now().toString();
        return isActiveOnDate(today);
    }

    // Helper method to get the effective date for display
    public String getEffectiveDate() {
        if (this.isSpecialSchedule && this.specialDate != null) {
            try {
                LocalDate date = LocalDate.parse(this.specialDate);
                return date.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy"));
            } catch (Exception e) {
                return this.specialDate;
            }
        } else if (this.displayDate != null) {
            try {
                LocalDate date = LocalDate.parse(this.displayDate);
                return date.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy"));
            } catch (Exception e) {
                return this.displayDate;
            }
        } else {
            return this.day; // Fallback to day name
        }
    }

    // Helper method to get formatted time range
    public String getFormattedTimeRange() {
        if (this.startTime != null && this.endTime != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            return this.startTime.format(formatter) + " - " + this.endTime.format(formatter);
        }
        return "";
    }

    // Helper method to validate the schedule
    public boolean isValid() {
        try {
            validateAndPrepareData();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String toString() {
        return "TimeTable{" +
                "id=" + id +
                ", subject='" + subject + '\'' +
                ", day='" + day + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", isSpecialSchedule=" + isSpecialSchedule +
                ", specialDate='" + specialDate + '\'' +
                ", location='" + location + '\'' +
                ", lecturer='" + lecturer + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TimeTable timeTable = (TimeTable) o;

        if (isSpecialSchedule != timeTable.isSpecialSchedule) return false;
        if (!subject.equals(timeTable.subject)) return false;
        if (startTime != null ? !startTime.equals(timeTable.startTime) : timeTable.startTime != null) return false;
        if (endTime != null ? !endTime.equals(timeTable.endTime) : timeTable.endTime != null) return false;
        if (isSpecialSchedule) {
            return specialDate != null ? specialDate.equals(timeTable.specialDate) : timeTable.specialDate == null;
        } else {
            return day != null ? day.equals(timeTable.day) : timeTable.day == null;
        }
    }

    @Override
    public int hashCode() {
        int result = subject.hashCode();
        result = 31 * result + (startTime != null ? startTime.hashCode() : 0);
        result = 31 * result + (endTime != null ? endTime.hashCode() : 0);
        if (isSpecialSchedule) {
            result = 31 * result + (specialDate != null ? specialDate.hashCode() : 0);
        } else {
            result = 31 * result + (day != null ? day.hashCode() : 0);
        }
        return result;
    }
}
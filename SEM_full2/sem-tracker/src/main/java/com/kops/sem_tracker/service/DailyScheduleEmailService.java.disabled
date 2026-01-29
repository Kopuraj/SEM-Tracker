package com.kops.sem_tracker.service;

import com.kops.sem_tracker.entyties.Student;
import com.kops.sem_tracker.entyties.TimeTable;
import com.kops.sem_tracker.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class DailyScheduleEmailService {

    @Autowired
    private TimeTableService timeTableService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private StudentRepository studentRepository;

    @Value("${app.schedule.daily-email.enabled:true}")
    private boolean dailyEmailEnabled;

    /**
     * Send daily schedule emails to all users
     * Runs every day at the configured time (default: 7:00 AM)
     * Cron format: second minute hour day month weekday
     * ${app.schedule.daily-email.time:07:00} would be "0 0 7 * * *"
     */
    @Scheduled(cron = "0 0 7 * * *") // Runs at 7:00 AM every day
    public void sendDailyScheduleToAllUsers() {
        if (!dailyEmailEnabled) {
            System.out.println("[DailyScheduleEmailService] Daily email service is disabled");
            return;
        }

        LocalDate today = LocalDate.now();
        String dayOfWeek = today.getDayOfWeek().toString();
        String dateString = today.toString();

        System.out.println("[DailyScheduleEmailService] Starting daily schedule email job for " + 
                         today.format(java.time.format.DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")));

        try {
            // Get all registered users
            List<Student> allUsers = studentRepository.findAll();
            
            int emailsSent = 0;
            int usersProcessed = 0;

            for (Student user : allUsers) {
                try {
                    usersProcessed++;
                    
                    // Get user's schedule for today (both regular and special)
                    List<TimeTable> todaySchedules = timeTableService.getTodaySchedulesForUser(user.getUsername());
                    
                    // Skip if user has no email
                    if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
                        System.out.println("[DailyScheduleEmailService] Skipping user " + user.getUsername() + 
                                         " (no email configured)");
                        continue;
                    }

                    // Send email even if no schedules (to inform user they have a free day)
                    emailService.sendDailyScheduleEmail(
                        user.getEmail(),
                        user.getUsername(),
                        todaySchedules
                    );
                    
                    emailsSent++;
                    System.out.println("[DailyScheduleEmailService] Sent schedule to " + user.getUsername() + 
                                     " (" + todaySchedules.size() + " classes)");

                } catch (Exception e) {
                    System.err.println("[DailyScheduleEmailService] Failed to send email to user " + 
                                     user.getUsername() + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }

            System.out.println("[DailyScheduleEmailService] Daily schedule email job completed. " +
                             "Processed: " + usersProcessed + " users, Sent: " + emailsSent + " emails");

        } catch (Exception e) {
            System.err.println("[DailyScheduleEmailService] Error in daily schedule email job: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Send schedule reminder before classes start
     * Runs every hour to check for upcoming classes
     */
    @Scheduled(cron = "0 0 * * * *") // Runs at the start of every hour
    public void sendUpcomingClassReminders() {
        if (!dailyEmailEnabled) {
            return;
        }

        try {
            LocalTime now = LocalTime.now();
            LocalTime oneHourLater = now.plusHours(1);

            // Get events starting in the next hour
            List<TimeTable> upcomingEvents = timeTableService.getEventsBetween(now, oneHourLater);

            for (TimeTable event : upcomingEvents) {
                if (event.getUsername() != null && !event.getUsername().isEmpty()) {
                    try {
                        Student user = studentRepository.findByUsername(event.getUsername()).orElse(null);
                        
                        if (user != null && user.getEmail() != null && !user.getEmail().isEmpty()) {
                            String subject = "🔔 Class Reminder: " + event.getSubject() + " starting soon";
                            String message = String.format(
                                "Hello %s,\n\n" +
                                "This is a reminder that your class is starting in less than an hour:\n\n" +
                                "Subject: %s\n" +
                                "Time: %s - %s\n" +
                                "Location: %s\n" +
                                "Lecturer: %s\n\n" +
                                "Have a great class!\n\n" +
                                "- SEM Tracker",
                                user.getUsername(),
                                event.getSubject(),
                                event.getStartTime(),
                                event.getEndTime(),
                                event.getLocation() != null ? event.getLocation() : "TBA",
                                event.getLecturer() != null ? event.getLecturer() : "TBA"
                            );

                            emailService.sendEmail(user.getEmail(), subject, message);
                        }
                    } catch (Exception e) {
                        System.err.println("[DailyScheduleEmailService] Failed to send reminder for event " + 
                                         event.getId() + ": " + e.getMessage());
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("[DailyScheduleEmailService] Error in reminder job: " + e.getMessage());
        }
    }

    /**
     * Manual trigger for testing (can be called from a controller endpoint)
     */
    public void sendScheduleToUser(String username) {
        try {
            Student user = studentRepository.findByUsername(username).orElse(null);
            
            if (user == null) {
                System.err.println("[DailyScheduleEmailService] User not found: " + username);
                return;
            }

            if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
                System.err.println("[DailyScheduleEmailService] User has no email configured: " + username);
                return;
            }

            List<TimeTable> todaySchedules = timeTableService.getTodaySchedulesForUser(username);
            emailService.sendDailyScheduleEmail(user.getEmail(), username, todaySchedules);
            
            System.out.println("[DailyScheduleEmailService] Manually sent schedule to " + username);

        } catch (Exception e) {
            System.err.println("[DailyScheduleEmailService] Failed to send schedule to " + username + ": " + 
                             e.getMessage());
            e.printStackTrace();
        }
    }
}

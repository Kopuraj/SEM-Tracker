package com.kops.sem_tracker.service;

import com.kops.sem_tracker.entyties.Student;
import com.kops.sem_tracker.entyties.TimeTable;
import com.kops.sem_tracker.repository.StudentRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class NotificationService {

    private final TimeTableService timetableService;
    private final EmailService emailService;
    private final PushNotificationService pushNotificationService;
    private final StudentRepository studentRepository;
    private final ConcurrentHashMap<Long, String> scheduledNotifications = new ConcurrentHashMap<>();

    public NotificationService(TimeTableService timetableService,
                               EmailService emailService,
                               PushNotificationService pushNotificationService,
                               StudentRepository studentRepository) {
        this.timetableService = timetableService;
        this.emailService = emailService;
        this.pushNotificationService = pushNotificationService;
        this.studentRepository = studentRepository;
    }

    public void scheduleNotification(TimeTable event) {
        // In a real implementation, you would schedule this with a task scheduler
        // For now, we'll just store it and check periodically
        scheduledNotifications.put(event.getId(), "SCHEDULED");
    }

    public void cancelNotification(TimeTable event) {
        scheduledNotifications.remove(event.getId());
    }

    // Check every minute for events that need notifications
    @Scheduled(fixedRate = 60000)
    public void checkForNotifications() {
        LocalTime now = LocalTime.now();
        LocalTime checkUntil = now.plusMinutes(15); // Check events in next 15 mins

        List<TimeTable> upcomingEvents = timetableService.getEventsBetween(now, checkUntil);

        for (TimeTable event : upcomingEvents) {
            if (scheduledNotifications.containsKey(event.getId())) {
                String status = scheduledNotifications.get(event.getId());

                if ("SCHEDULED".equals(status)) {
                    sendNotification(event);
                    scheduledNotifications.put(event.getId(), "SENT");
                }
            }
        }
    }

    private void sendNotification(TimeTable event) {
        String message = String.format("Reminder: %s starts at %s",
                event.getTitle(), event.getStartTime());

        String preference = event.getNotificationPreference();

        if ("EMAIL".equals(preference) || "BOTH".equals(preference)) {
            String email = null;
            if (event.getUsername() != null) {
                Optional<Student> studentOpt = studentRepository.findByUsername(event.getUsername());
                if (studentOpt.isPresent()) {
                    email = studentOpt.get().getEmail();
                }
            }
            if (email != null && !email.isBlank()) {
                emailService.sendEmail(email, "Class Reminder", message);
            }
        }

        if ("PUSH".equals(preference) || "BOTH".equals(preference)) {
            pushNotificationService.sendPushNotification(event.getDeviceToken(), message);
        }
    }
}
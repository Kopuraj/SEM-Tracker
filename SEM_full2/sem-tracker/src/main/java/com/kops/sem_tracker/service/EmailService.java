package com.kops.sem_tracker.service;

import com.kops.sem_tracker.entyties.TimeTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class EmailService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${app.email.from:noreply@semtracker.com}")
    private String fromEmail;

    @Value("${app.email.enabled:false}")
    private boolean emailEnabled;

    /**
     * Send a simple text email
     */
    public void sendEmail(String to, String subject, String text) {
        if (!emailEnabled || mailSender == null) {
            System.out.println("[EmailService] Email disabled or not configured. Would send to=" + to + 
                             ", subject=" + subject);
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
            System.out.println("[EmailService] Email sent successfully to " + to);
        } catch (Exception e) {
            System.err.println("[EmailService] Failed to send email: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Send daily schedule email with HTML formatting
     */
    public void sendDailyScheduleEmail(String to, String username, List<TimeTable> schedules) {
        if (!emailEnabled || mailSender == null) {
            System.out.println("[EmailService] Email disabled. Would send daily schedule to " + to);
            logSchedulePreview(username, schedules);
            return;
        }

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(to);
            
            LocalDate today = LocalDate.now();
            String formattedDate = today.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy"));
            helper.setSubject("📅 Your Schedule for " + formattedDate);
            
            String htmlContent = buildDailyScheduleHtml(username, schedules, today);
            helper.setText(htmlContent, true);
            
            mailSender.send(mimeMessage);
            System.out.println("[EmailService] Daily schedule email sent to " + to);
        } catch (MessagingException e) {
            System.err.println("[EmailService] Failed to send daily schedule email: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Build HTML content for daily schedule email
     */
    private String buildDailyScheduleHtml(String username, List<TimeTable> schedules, LocalDate date) {
        StringBuilder html = new StringBuilder();
        String formattedDate = date.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy"));
        
        html.append("<!DOCTYPE html><html><head><style>");
        html.append("body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }");
        html.append(".container { max-width: 600px; margin: 0 auto; padding: 20px; }");
        html.append(".header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 30px; border-radius: 10px 10px 0 0; text-align: center; }");
        html.append(".content { background: #f9f9f9; padding: 20px; }");
        html.append(".schedule-item { background: white; padding: 15px; margin: 10px 0; border-left: 4px solid #667eea; border-radius: 5px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }");
        html.append(".special { border-left-color: #ff6b6b; }");
        html.append(".time { font-weight: bold; color: #667eea; font-size: 16px; }");
        html.append(".subject { font-size: 18px; font-weight: bold; margin: 5px 0; }");
        html.append(".details { color: #666; font-size: 14px; }");
        html.append(".no-schedule { text-align: center; padding: 40px; color: #999; }");
        html.append(".footer { background: #333; color: white; padding: 20px; text-align: center; border-radius: 0 0 10px 10px; font-size: 12px; }");
        html.append("</style></head><body>");
        
        html.append("<div class='container'>");
        html.append("<div class='header'>");
        html.append("<h1>📅 Today's Schedule</h1>");
        html.append("<p>").append(formattedDate).append("</p>");
        html.append("<p>Hello, ").append(username).append("!</p>");
        html.append("</div>");
        
        html.append("<div class='content'>");
        
        if (schedules == null || schedules.isEmpty()) {
            html.append("<div class='no-schedule'>");
            html.append("<h2>🎉 No Classes Today!</h2>");
            html.append("<p>Enjoy your free day!</p>");
            html.append("</div>");
        } else {
            html.append("<h2 style='color: #667eea;'>You have ").append(schedules.size())
                .append(" class").append(schedules.size() > 1 ? "es" : "").append(" today:</h2>");
            
            // Sort schedules by start time
            schedules.sort((a, b) -> {
                LocalTime timeA = a.getStartTime();
                LocalTime timeB = b.getStartTime();
                return timeA != null && timeB != null ? timeA.compareTo(timeB) : 0;
            });
            
            for (TimeTable schedule : schedules) {
                String specialClass = schedule.isSpecialSchedule() ? " special" : "";
                html.append("<div class='schedule-item").append(specialClass).append("'>");
                
                if (schedule.isSpecialSchedule()) {
                    html.append("<span style='background: #ff6b6b; color: white; padding: 3px 8px; border-radius: 3px; font-size: 11px; font-weight: bold;'>SPECIAL</span> ");
                }
                
                html.append("<div class='time'>⏰ ");
                if (schedule.getStartTime() != null && schedule.getEndTime() != null) {
                    html.append(formatTime(schedule.getStartTime())).append(" - ").append(formatTime(schedule.getEndTime()));
                }
                html.append("</div>");
                
                html.append("<div class='subject'>").append(escapeHtml(schedule.getSubject())).append("</div>");
                
                if (schedule.getLecturer() != null && !schedule.getLecturer().isEmpty()) {
                    html.append("<div class='details'>👤 ").append(escapeHtml(schedule.getLecturer())).append("</div>");
                }
                
                if (schedule.getLocation() != null && !schedule.getLocation().isEmpty()) {
                    html.append("<div class='details'>📍 ").append(escapeHtml(schedule.getLocation())).append("</div>");
                }
                
                if (schedule.getDescription() != null && !schedule.getDescription().isEmpty()) {
                    html.append("<div class='details'>📝 ").append(escapeHtml(schedule.getDescription())).append("</div>");
                }
                
                html.append("</div>");
            }
        }
        
        html.append("</div>");
        
        html.append("<div class='footer'>");
        html.append("<p>This is an automated email from SEM Tracker</p>");
        html.append("<p>Have a great day! 🎓</p>");
        html.append("</div>");
        
        html.append("</div>");
        html.append("</body></html>");
        
        return html.toString();
    }

    /**
     * Format time for display
     */
    private String formatTime(LocalTime time) {
        if (time == null) return "";
        return time.format(DateTimeFormatter.ofPattern("hh:mm a"));
    }

    /**
     * Escape HTML special characters
     */
    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#39;");
    }

    /**
     * Log schedule preview when email is disabled
     */
    private void logSchedulePreview(String username, List<TimeTable> schedules) {
        System.out.println("\n========== DAILY SCHEDULE PREVIEW for " + username + " ==========");
        System.out.println("Date: " + LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")));
        
        if (schedules == null || schedules.isEmpty()) {
            System.out.println("No classes scheduled for today!");
        } else {
            System.out.println("Total classes: " + schedules.size());
            schedules.sort((a, b) -> {
                LocalTime timeA = a.getStartTime();
                LocalTime timeB = b.getStartTime();
                return timeA != null && timeB != null ? timeA.compareTo(timeB) : 0;
            });
            
            for (int i = 0; i < schedules.size(); i++) {
                TimeTable schedule = schedules.get(i);
                System.out.println("\n" + (i + 1) + ". " + schedule.getSubject() + 
                                 (schedule.isSpecialSchedule() ? " [SPECIAL]" : ""));
                System.out.println("   Time: " + formatTime(schedule.getStartTime()) + 
                                 " - " + formatTime(schedule.getEndTime()));
                if (schedule.getLecturer() != null && !schedule.getLecturer().isEmpty()) {
                    System.out.println("   Lecturer: " + schedule.getLecturer());
                }
                if (schedule.getLocation() != null && !schedule.getLocation().isEmpty()) {
                    System.out.println("   Location: " + schedule.getLocation());
                }
            }
        }
        System.out.println("============================================================\n");
    }
}

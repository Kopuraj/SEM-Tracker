package com.kops.sem_tracker.service;

import org.springframework.stereotype.Service;

@Service
public class EmailService {

    // No-op email sender to avoid Spring Mail dependency
    public void sendEmail(String to, String subject, String text) {
        System.out.println("[EmailService] Pretend send to=" + to + ", subject=" + subject + ", text=" + text);
    }
}

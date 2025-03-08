package com.example.pricetracker.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailNotificationService {

    private final JavaMailSender mailSender;

    public EmailNotificationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendPriceDropAlert(String recipient, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(recipient);
        message.setSubject(subject);
        message.setText(text);

        mailSender.send(message);
        System.out.println("Email sent to " + recipient);
    }
}

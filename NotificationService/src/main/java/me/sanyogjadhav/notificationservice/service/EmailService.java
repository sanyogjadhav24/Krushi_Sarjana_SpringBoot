package me.sanyogjadhav.notificationservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendEmail(
            String recipient,
            String subject,
            String message
    ) {

        if (recipient == null || recipient.isBlank()) {
            return;
        }

        SimpleMailMessage email = new SimpleMailMessage();

        email.setTo(recipient);
        email.setSubject(subject);
        email.setText(message);

        mailSender.send(email);
    }
}
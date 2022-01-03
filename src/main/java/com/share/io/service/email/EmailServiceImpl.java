package com.share.io.service.email;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender emailSender;

    public EmailServiceImpl(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void sendSimpleMessage(String userEmail, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        //Not working with google
        message.setFrom("noreply@shareio.com");
        message.setTo(userEmail);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
    }
}

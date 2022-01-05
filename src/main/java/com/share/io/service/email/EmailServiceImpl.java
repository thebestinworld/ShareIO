package com.share.io.service.email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender emailSender;
    @Value("${share.io.sendmail}")
    private Boolean sendEmail;

    public EmailServiceImpl(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    @Async
    @Override
    public void sendSimpleMessage(String userEmail, String subject, String text) {
        if (!sendEmail) {
            return;
        }
        SimpleMailMessage message = new SimpleMailMessage();
        //Not working with google
        message.setFrom("noreply@shareio.com");
        message.setTo(userEmail);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
    }
}

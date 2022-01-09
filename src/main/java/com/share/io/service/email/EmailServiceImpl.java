package com.share.io.service.email;

import com.share.io.dto.email.EmailSubject;
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
    public void sendSimpleMessage(Long userId, Long fromId, String userEmail, EmailSubject subject, String username, Long fileId) {
        if (!sendEmail) {
            return;
        }
        if (userId.equals(fromId)) {
            return;
        }
        SimpleMailMessage message = new SimpleMailMessage();
        //Not working with google api
        message.setFrom("noreply@shareio.com");
        message.setTo(userEmail);
        message.setSubject(String.format(subject.getSubject(), fileId));
        message.setText(generateMessage(subject, username, fileId));
        emailSender.send(message);
    }

    private String generateMessage(EmailSubject subject, String username, Long fileId) {
        switch (subject) {
            case FILE_UPDATE:
                return String.format("File %d has been updated by %s", fileId, username);
            default:
                return "";
        }
    }
}

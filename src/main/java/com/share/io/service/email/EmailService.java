package com.share.io.service.email;

public interface EmailService {

    void sendSimpleMessage(String userEmail, String subject, String text);
}

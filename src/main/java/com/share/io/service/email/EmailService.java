package com.share.io.service.email;

import com.share.io.dto.email.EmailSubject;

public interface EmailService {

    void sendSimpleMessage(Long userId, Long fromId, String userEmail,
                           EmailSubject subject, String username, Long fileId);
}

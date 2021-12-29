package com.share.io.service.notification;

import com.share.io.model.notification.Notification;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class NotificationServiceImpl implements NotificationService{

    private final SimpMessagingTemplate template;

    public NotificationServiceImpl(SimpMessagingTemplate template) {
        this.template = template;
    }

    //TODO: Save notification into database
    public void sendNotification() {
        Notification notification = new Notification();
        notification.setId(1L);
        notification.setUserId(1L);
        Random random = new Random();
        String message = "Neymar на CAM или ST да го играя " + random.nextBoolean();
        notification.setMessage(message);
        notification.setFromUserId(2L);
        notification.setReceivedDate(LocalDateTime.now());
        template.convertAndSend("/topic/notification", notification);
    }
}

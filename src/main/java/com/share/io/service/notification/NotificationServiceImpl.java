package com.share.io.service.notification;

import com.share.io.model.notification.Notification;
import com.share.io.model.notification.NotificationType;
import com.share.io.repository.notification.NotificationRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService{

    private final SimpMessagingTemplate template;
    private final NotificationRepository notificationRepository;

    public NotificationServiceImpl(SimpMessagingTemplate template, NotificationRepository notificationRepository) {
        this.template = template;
        this.notificationRepository = notificationRepository;
    }

    public void sendNotification(NotificationType notificationType, String fileName, String fileId,
                                 Long userId, Long fromUserId, String username) {
        if (userId.equals(fromUserId)) {
            // No need to send notification for user the same user
            return;
        }
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setFromUserId(fromUserId);
        notification.setFileId(fileId);
        notification.setMessage(generateMessage(notificationType, fileName, username));
        notification.setReceivedDate(LocalDateTime.now());
        notification.setNotificationType(notificationType);
        notificationRepository.save(notification);
        template.convertAndSend("/topic/notification", notification);
    }

    private String generateMessage(NotificationType notificationType, String fileName, String username){
        switch (notificationType){
            case FILE_UPDATED:
                return String.format("File %s has been updated by %s", fileName, username);
            case FILE_SHARED:
                return String.format("File %s has been shared by %s", fileName, username);
            case FILE_DELETED:
                return String.format("File %s has been deleted by %s", fileName, username);
            default:
                return "Default message";
        }
    }

    @Override
    public List<Notification> findAllByUserId(Long userId) {
        return this.notificationRepository.findAllByUserId(userId);
    }
}

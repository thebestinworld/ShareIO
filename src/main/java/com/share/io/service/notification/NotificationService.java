package com.share.io.service.notification;

import com.share.io.model.notification.Notification;
import com.share.io.model.notification.NotificationType;

import java.util.List;

public interface NotificationService {

    void sendNotification(NotificationType notificationType, String fileName, String fileId,
                          Long userId, Long fromUserId, String username);

    List<Notification> findAllByUserId(Long userId);
}

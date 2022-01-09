package com.share.io.service.notification;

import com.share.io.dto.query.notification.NotificationQuery;
import com.share.io.model.notification.Notification;
import com.share.io.model.notification.NotificationType;
import org.springframework.data.domain.Page;

public interface NotificationService {

    void sendNotification(NotificationType notificationType, String fileName, Long fileId,
                          Long userId, Long fromUserId, String username);

    Page<Notification> findAllByUserId(Long userId, NotificationQuery notificationQuery);

    Notification markAsRead(Long id);
}

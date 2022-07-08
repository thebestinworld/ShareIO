package com.share.io.service.notification;

import static com.share.io.repository.notification.NotificationSpecification.idEquals;
import static com.share.io.repository.notification.NotificationSpecification.isRead;
import static com.share.io.repository.notification.NotificationSpecification.messageContains;
import static com.share.io.repository.notification.NotificationSpecification.receivedDateContains;
import static com.share.io.repository.notification.NotificationSpecification.sort;
import static com.share.io.repository.notification.NotificationSpecification.userIdEquals;
import com.share.io.dto.query.notification.NotificationQuery;
import com.share.io.exception.ApiException;
import com.share.io.model.notification.Notification;
import com.share.io.model.notification.NotificationType;
import com.share.io.repository.notification.NotificationRepository;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


@Service
public class NotificationServiceImpl implements NotificationService {

    private final SimpMessagingTemplate template;
    private final NotificationRepository notificationRepository;

    public NotificationServiceImpl(SimpMessagingTemplate template, NotificationRepository notificationRepository) {
        this.template = template;
        this.notificationRepository = notificationRepository;
    }

    @Async
    @Override
    public void sendNotification(NotificationType notificationType, String fileName, Long fileId,
                                 Long userId, Long fromUserId, String username) {
        if (userId.equals(fromUserId)) {
            // No need to send notification for user the same user
            return;
        }
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setFromUserId(fromUserId);
        notification.setFileId(fileId);
        notification.setMessage(generateMessage(notificationType, fileId, username));
        notification.setReceivedDate(LocalDateTime.now());
        notification.setNotificationType(notificationType);
        notificationRepository.save(notification);
        template.convertAndSend("/topic/notification", notification);
    }

    private String generateMessage(NotificationType notificationType, Long fileId, String username) {
        switch (notificationType) {
            case FILE_UPDATED:
                return String.format("File %d has been updated by %s", fileId, username);
            case FILE_SHARED:
                return String.format("File %d has been shared by %s", fileId, username);
            case FILE_DELETED:
                return String.format("File %d has been deleted by %s", fileId, username);
            case FILE_REVERTED:
                return String.format("File %d has been reverted by %s", fileId, username);
            default:
                return "Default message";
        }
    }

    @Override
    public Page<Notification> findAllByUserId(Long userId, NotificationQuery notificationQuery) {
        Specification<Notification> specification = userIdEquals(userId)
                .and(messageContains(notificationQuery.getMessage()))
                .and(idEquals(notificationQuery.getId()))
                .and(receivedDateContains(notificationQuery.getReceivedDate()))
                .and(isRead(notificationQuery.getIsRead()))
                .and(sort(notificationQuery.getSort(), notificationQuery.getOrder()));

        return notificationRepository.findAll(specification,
                PageRequest.of(notificationQuery.getPage(), notificationQuery.getSize()));
    }

    @Override
    @Transactional
    public Notification markAsRead(Long id) {
        Notification notification = this.notificationRepository.findById(id)
                .orElseThrow(() -> new ApiException("Notification does not exist!"));
        notification.setRead(true);
        return notificationRepository.save(notification);
    }
}

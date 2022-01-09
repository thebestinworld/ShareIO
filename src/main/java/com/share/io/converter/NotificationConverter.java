package com.share.io.converter;

import com.share.io.dto.notification.NotificationDTO;
import com.share.io.model.notification.Notification;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class NotificationConverter {

    public List<NotificationDTO> convert(Collection<Notification> notifications) {
        return notifications.stream().map(this::convert)
                .collect(Collectors.toList());
    }

    public NotificationDTO convert(Notification notification) {
        NotificationDTO notificationDTO = new NotificationDTO();
        notificationDTO.setId(notification.getId());
        notificationDTO.setMessage(notification.getMessage());
        String time = notification.getReceivedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        notificationDTO.setReceivedDate(time);
        String isRead = notification.isRead() ? "Yes" : "No";
        notificationDTO.setIsRead(isRead);
        notificationDTO.setUserId(notification.getUserId());
        notificationDTO.setFromUserId(notification.getFromUserId());
        return notificationDTO;
    }
}

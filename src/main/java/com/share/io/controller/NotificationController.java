package com.share.io.controller;

import com.share.io.converter.NotificationConverter;
import com.share.io.dto.notification.NotificationViewDTO;
import com.share.io.dto.query.notification.NotificationQuery;
import com.share.io.model.notification.Notification;
import com.share.io.security.CurrentUser;
import com.share.io.security.UserCurrent;
import com.share.io.service.notification.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/notification")
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationConverter notificationConverter;

    public NotificationController(NotificationService notificationService, NotificationConverter notificationConverter) {
        this.notificationService = notificationService;
        this.notificationConverter = notificationConverter;
    }

    @PostMapping("/all")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<NotificationViewDTO> findAllNotification(@CurrentUser UserCurrent userCurrent,
                                                                   @RequestBody NotificationQuery notificationQuery) {
        Page<Notification> result = notificationService.findAllByUserId(userCurrent.getId(), notificationQuery);
        NotificationViewDTO notificationViewDTO = new NotificationViewDTO();
        notificationViewDTO.setItems(notificationConverter.convert(result.getContent()));
        notificationViewDTO.setTotalCount(result.getTotalElements());
        return ResponseEntity.status(HttpStatus.OK).body(notificationViewDTO);
    }

    @PostMapping("/{id}/mark-as-read")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> markAsRead(@PathVariable Long id) {
        this.notificationService.markAsRead(id);
        return ResponseEntity.status(HttpStatus.OK).body("Notification marked as read");
    }
}

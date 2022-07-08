package com.share.io.controller;

import static com.share.io.dto.email.EmailSubject.FILE_REVERTED;
import static com.share.io.model.eventlog.Event.REVERTED;
import com.share.io.dto.file.undo.RevertDTO;
import com.share.io.dto.response.MessageResponse;
import com.share.io.model.file.File;
import com.share.io.model.notification.NotificationType;
import com.share.io.model.user.User;
import com.share.io.security.CurrentUser;
import com.share.io.security.UserCurrent;
import com.share.io.service.email.EmailService;
import com.share.io.service.file.FileService;
import com.share.io.service.file.undo.UndoService;
import com.share.io.service.log.FileEventLogServiceImpl;
import com.share.io.service.notification.NotificationService;
import java.util.List;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/revert")
public class RevertController {

    private final UndoService undoService;
    private final FileService fileService;
    private final EmailService emailService;
    private final NotificationService notificationService;
    private final FileEventLogServiceImpl eventLogService;

    public RevertController(UndoService undoService,
                            FileService fileService,
                            EmailService emailService,
                            NotificationService notificationService,
                            FileEventLogServiceImpl eventLogService) {
        this.undoService = undoService;
        this.fileService = fileService;
        this.emailService = emailService;
        this.notificationService = notificationService;
        this.eventLogService = eventLogService;
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<Long>> getFileVersions(@PathVariable Long id) {
        File fileById = fileService.getFileById(id);
        return ResponseEntity.status(HttpStatus.OK).body(undoService.getFileVersions(id, fileById.getVersion()));
    }

    @PostMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> revertFile(@PathVariable Long id, @RequestBody RevertDTO version,
                                                      @CurrentUser UserCurrent userCurrent) {
        File file = fileService.getFileById(id);
        undoService.saveSnap(undoService.generateSnap(file));
        Set<User> usersToSendNotification = file.getSharedUsers();
        usersToSendNotification.add(file.getUploader());
        for (User user : usersToSendNotification) {
            notificationService.sendNotification(NotificationType.FILE_REVERTED,
                    file.getName(), file.getId(), user.getId(), userCurrent.getId(), userCurrent.getName());
            emailService.sendMessage(user.getId(), userCurrent.getId(), user.getEmail(),
                    FILE_REVERTED, userCurrent.getName(), file.getId());
        }
        eventLogService.logEvent(id, REVERTED, userCurrent.getName());
        undoService.revertFile(userCurrent, id, version.getVersionId());
        return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse("Revert Successful"));
    }
}

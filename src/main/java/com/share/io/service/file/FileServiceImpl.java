package com.share.io.service.file;

import static com.share.io.dto.email.EmailSubject.FILE_DELETE;
import static com.share.io.dto.email.EmailSubject.FILE_SHARE;
import static com.share.io.repository.file.FileSpecification.contentTypeContains;
import static com.share.io.repository.file.FileSpecification.descriptionContains;
import static com.share.io.repository.file.FileSpecification.extensionContains;
import static com.share.io.repository.file.FileSpecification.fileType;
import static com.share.io.repository.file.FileSpecification.idEquals;
import static com.share.io.repository.file.FileSpecification.nameContains;
import static com.share.io.repository.file.FileSpecification.originalNameContains;
import static com.share.io.repository.file.FileSpecification.sharedUsersContain;
import static com.share.io.repository.file.FileSpecification.sort;
import static com.share.io.repository.file.FileSpecification.updateDateContains;
import static com.share.io.repository.file.FileSpecification.uploadDateContains;
import static com.share.io.repository.file.FileSpecification.uploaderIdEquals;
import static com.share.io.repository.file.FileSpecification.uploaderNameContains;
import static com.share.io.repository.file.FileSpecification.version;
import com.share.io.dto.email.EmailSubject;
import com.share.io.dto.file.FileUpdateDTO;
import com.share.io.dto.file.FileUploadDTO;
import com.share.io.dto.query.file.FileQuery;
import com.share.io.exception.ApiException;
import com.share.io.model.eventlog.Event;
import com.share.io.model.file.File;
import com.share.io.model.file.FileType;
import com.share.io.model.file.undo.FileSnap;
import com.share.io.model.notification.NotificationType;
import com.share.io.model.user.User;
import com.share.io.repository.file.FileRepository;
import com.share.io.repository.user.UserRepository;
import com.share.io.security.UserCurrent;
import com.share.io.service.email.EmailService;
import com.share.io.service.file.undo.UndoService;
import com.share.io.service.log.FileEventLogService;
import com.share.io.service.notification.NotificationService;
import javax.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileServiceImpl implements FileService {

    private final FileRepository fileRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final EmailService emailService;
    private final UndoService undoService;
    private final FileEventLogService eventLogService;

    public FileServiceImpl(FileRepository fileRepository,
                           UserRepository userRepository,
                           NotificationService notificationService,
                           EmailService emailService,
                           UndoService undoService,
                           FileEventLogService eventLogService) {
        this.fileRepository = fileRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
        this.emailService = emailService;
        this.undoService = undoService;
        this.eventLogService = eventLogService;
    }

    @Override
    public File save(Long id, MultipartFile fileData, Long uploaderId) {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(fileData.getOriginalFilename()));
        File file = fileRepository.findById(id).orElseThrow(() -> new ApiException("File does not exist!"));
        try {
            file.setOriginalName(fileName);
            file.setContentType(fileData.getContentType());
            file.setData(fileData.getBytes());
            file.setExtension(StringUtils.getFilenameExtension(fileData.getOriginalFilename()));
            file.setFileType(FileType.getFileTypeFromExtension(file.getExtension()));
        } catch (IOException e) {
            throw new ApiException("Error when saving file data!");
        }

        return fileRepository.save(file);
    }

    @Override
    public File saveFileMetadata(FileUploadDTO fileDTO, Long uploaderId) {
        User user = userRepository.findById(uploaderId).orElseThrow(() -> new ApiException("User does not exist!"));
        File file = new File();
        file.setName(fileDTO.getName());
        file.setDescription(fileDTO.getDescription());
        file.setUploader(user);
        file.setVersion(1L);
        file.setLastVersion(1L);
        file.setUploadDate(LocalDateTime.now());
        File save = fileRepository.save(file);
        eventLogService.logEvent(file.getId(), Event.UPLOADED, user.getUsername());
        return save;
    }

    @Override
    public File update(Long id, MultipartFile fileData, UserCurrent userCurrent) {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(fileData.getOriginalFilename()));
        File file = fileRepository.findById(id).orElseThrow(() -> new ApiException("File does not exist!"));
        try {
            undoService.updateFileData(file, id, file.getLastVersion());
            file.setOriginalName(fileName);
            file.setContentType(fileData.getContentType());
            file.setData(fileData.getBytes());
            file.setExtension(StringUtils.getFilenameExtension(fileData.getOriginalFilename()));
            file.setFileType(FileType.getFileTypeFromExtension(file.getExtension()));
        } catch (IOException e) {
            throw new ApiException("Error when updating file data!");
        }

        return sendUpdateFileNotification(userCurrent, file);
    }

    private File sendUpdateFileNotification(UserCurrent userCurrent, File file) {
        File save = fileRepository.save(file);
        Set<User> usersToSendNotification = file.getSharedUsers();
        usersToSendNotification.add(save.getUploader());
        for (User user : usersToSendNotification) {
            notificationService.sendNotification(NotificationType.FILE_UPDATED,
                    file.getName(), file.getId(), user.getId(), userCurrent.getId(), userCurrent.getName());
            emailService.sendMessage(user.getId(), userCurrent.getId(), user.getEmail(),
                    EmailSubject.FILE_UPDATE, userCurrent.getName(), save.getId());
        }
        return save;
    }

    @Override
    public File updateFileMetaData(Long id, FileUpdateDTO fileUpdateDTO, UserCurrent userCurrent) {
        File file = fileRepository.findById(id).orElseThrow(() -> new ApiException("File does not exist!"));
        FileSnap fileSnap = undoService.generateSnap(file);
        file.setUpdateDate(LocalDateTime.now());
        file.setName(fileUpdateDTO.getName());
        file.setDescription(fileUpdateDTO.getDescription());
        file.setLastVersion(file.getLastVersion() + 1L);
        file.setVersion(file.getLastVersion());
        File save = sendUpdateFileNotification(userCurrent, file);
        undoService.saveSnap(fileSnap);
        eventLogService.logEvent(save.getId(), Event.UPDATED, userCurrent.getName());
        return save;
    }

    @Override
    @Transactional
    public void deleteFile(Long id, UserCurrent userCurrent) {
        Optional<File> file = this.fileRepository.findById(id);
        if (file.isPresent()) {
            Set<User> usersToSendNotification = file.get().getSharedUsers();
            usersToSendNotification.add(file.get().getUploader());
            for (User user : usersToSendNotification) {
                notificationService.sendNotification(NotificationType.FILE_DELETED,
                        file.get().getName(), file.get().getId(), user.getId(), userCurrent.getId(), userCurrent.getName());
                emailService.sendMessage(user.getId(), userCurrent.getId(), user.getEmail(),
                        FILE_DELETE, userCurrent.getName(), file.get().getId());
            }
            undoService.cleanHistory(file.get().getId());
            eventLogService.deleteEventLogsByFileId(file.get().getId());
            fileRepository.delete(file.get());
        }
    }

    @Override
    public void shareFile(Long id, Long userId, Long sharedToUserId, UserCurrent userCurrent) {
        File file = fileRepository.findById(id).orElseThrow(() -> new ApiException("File does not exist!"));
        User user = userRepository.findById(sharedToUserId).orElseThrow(() -> new ApiException("User does not exist!"));
        file.addShareUser(user);

        notificationService.sendNotification(NotificationType.FILE_SHARED,
                file.getName(), file.getId(), sharedToUserId, userCurrent.getId(), userCurrent.getName());
        emailService.sendMessage(file.getUploader().getId(),
                userCurrent.getId(), file.getUploader().getEmail(),
                FILE_SHARE,
                userCurrent.getEmail(), file.getId());
        eventLogService.logEvent(file.getId(), Event.SHARED, userCurrent.getName());
        fileRepository.save(file);
    }


    @Override
    public Page<File> findAllFilesBySpecification(FileQuery fileQuery) {
        Specification<File> specification = uploaderIdEquals(fileQuery.getUserId())
                .or(sharedUsersContain(fileQuery.getUserId()))
                .and(nameContains(fileQuery.getName()))
                .and(idEquals(fileQuery.getId()))
                .and(originalNameContains(fileQuery.getOriginalName()))
                .and(descriptionContains(fileQuery.getDescription()))
                .and(fileType(FileType.getFileType(fileQuery.getFileType())))
                .and(contentTypeContains(fileQuery.getContentType()))
                .and(extensionContains(fileQuery.getExtension()))
                .and(version(fileQuery.getVersion()))
                .and(uploadDateContains(fileQuery.getUploadDate()))
                .and(updateDateContains(fileQuery.getUpdateDate()))
                .and(uploaderNameContains(fileQuery.getUploaderName()))
                .and(sort(fileQuery.getSort(), fileQuery.getOrder()));

        return fileRepository.findAll(specification, PageRequest.of(fileQuery.getPage(), fileQuery.getSize()));
    }

    @Override
    public File getFileById(Long id) {
        return fileRepository.findById(id).orElseThrow(() -> new ApiException("File does not exist!"));
    }
}

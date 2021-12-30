package com.share.io.service.file;

import com.share.io.dto.file.FileUpdateDTO;
import com.share.io.dto.file.FileUploadDTO;
import com.share.io.dto.query.file.FileQuery;
import com.share.io.model.file.File;
import com.share.io.model.file.FileType;
import com.share.io.model.file.File_;
import com.share.io.model.notification.NotificationType;
import com.share.io.model.user.User;
import com.share.io.repository.file.FileRepository;
import com.share.io.repository.user.UserRepository;
import com.share.io.security.UserCurrent;
import com.share.io.service.notification.NotificationService;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static com.share.io.repository.file.FileSpecification.contentTypeContains;
import static com.share.io.repository.file.FileSpecification.descriptionContains;
import static com.share.io.repository.file.FileSpecification.extensionContains;
import static com.share.io.repository.file.FileSpecification.fileType;
import static com.share.io.repository.file.FileSpecification.nameContains;
import static com.share.io.repository.file.FileSpecification.originalNameContains;
import static com.share.io.repository.file.FileSpecification.sharedUsersContain;
import static com.share.io.repository.file.FileSpecification.uploaderIdEquals;
import static com.share.io.repository.file.FileSpecification.version;
import static com.share.io.util.SqlUtil.betweenSpec;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private final FileRepository fileRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public FileStorageServiceImpl(FileRepository fileRepository,
                                  UserRepository userRepository,
                                  NotificationService notificationService) {
        this.fileRepository = fileRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    @Override
    public File save(String id, MultipartFile fileData, Long uploaderId) {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(fileData.getOriginalFilename()));
        //TODO: Add custom exceptions
        File file = fileRepository.findById(id).orElseThrow(() -> new RuntimeException());
        try {
            file.setOriginalName(fileName);
            file.setContentType(fileData.getContentType());
            file.setData(fileData.getBytes());
            file.setExtension(StringUtils.getFilenameExtension(fileData.getOriginalFilename()));
            file.setFileType(FileType.getFileType(file.getExtension()));
        } catch (IOException e) {
            throw new RuntimeException();
        }

        return fileRepository.save(file);
    }

    @Override
    public File saveFileMetadata(FileUploadDTO fileDTO, Long uploaderId) {
        User user = userRepository.findById(uploaderId).orElseThrow(() -> new RuntimeException());
        File file = new File();
        file.setName(fileDTO.getName());
        file.setDescription(fileDTO.getDescription());
        file.setUploader(user);
        file.setVersion(1L);
        file.setUploadDate(LocalDateTime.now());
        return fileRepository.save(file);
    }

    @Override
    public File update(String id, MultipartFile fileData, UserCurrent userCurrent) {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(fileData.getOriginalFilename()));
        //TODO: Add custom exceptions
        File file = fileRepository.findById(id).orElseThrow(() -> new RuntimeException());
        try {
            file.setOriginalName(fileName);
            file.setContentType(fileData.getContentType());
            file.setData(fileData.getBytes());
            file.setExtension(StringUtils.getFilenameExtension(fileData.getOriginalFilename()));
            file.setFileType(FileType.getFileType(file.getExtension()));
        } catch (IOException e) {
            throw new RuntimeException();
        }

        File result = fileRepository.save(file);
        notificationService.sendNotification(NotificationType.FILE_UPDATED,
                file.getName(), file.getId(), file.getUploader().getId(), userCurrent.getId(), userCurrent.getName());
        return result;
    }

    @Override
    public File updateFileMetaData(String id, FileUpdateDTO fileUpdateDTO, UserCurrent userCurrent) {
        File file = fileRepository.findById(id).orElseThrow(() -> new RuntimeException());
        file.setUpdateDate(LocalDateTime.now());
        file.setName(fileUpdateDTO.getName());
        file.setDescription(fileUpdateDTO.getDescription());
        file.setVersion(file.getVersion() + 1L);
        File save = fileRepository.save(file);
        notificationService.sendNotification(NotificationType.FILE_UPDATED,
                file.getName(), file.getId(), file.getUploader().getId(), userCurrent.getId(), userCurrent.getName());
        return save;
    }

    @Override
    public void deleteFile(String id, UserCurrent userCurrent) {
        Optional<File> file = this.fileRepository.findById(id);
        file.ifPresent(value -> notificationService.sendNotification(NotificationType.FILE_DELETED,
                value.getName(), value.getId(), value.getUploader().getId(),
                userCurrent.getId(), userCurrent.getName()));
        this.fileRepository.deleteById(id);
    }

    @Override
    public void shareFile(String id, Long userId, Long sharedToUserId, UserCurrent userCurrent) {
        //TODO: Add logging
        File file = fileRepository.findById(id).orElseThrow(() -> new RuntimeException());
        User user = userRepository.findById(sharedToUserId).orElseThrow(() -> new RuntimeException());
        user.addSharedFile(file);
        userRepository.save(user);
        notificationService.sendNotification(NotificationType.FILE_SHARED,
                file.getName(), file.getId(), sharedToUserId, userCurrent.getId(), userCurrent.getName());
    }


    public Collection<File> findAllFilesBySpecification(FileQuery fileQuery) {
        Specification<File> specification = uploaderIdEquals(fileQuery.getUserId())
                .or(sharedUsersContain(fileQuery.getUserId()))
                .and(originalNameContains(fileQuery.getOriginalName()))
                .and(nameContains(fileQuery.getName()))
                .and(descriptionContains(fileQuery.getDescription()))
                .and(fileType(fileQuery.getFileType()))
                .and(contentTypeContains(fileQuery.getContentType()))
                .and(extensionContains(fileQuery.getExtension()))
                .and(version(fileQuery.getVersion()))
                .and(betweenSpec(File_.uploadDate, fileQuery.getUploadDate()));

        List<File> files = fileRepository.findAll(specification);

        return files;
    }

    @Override
    public File getFileById(String id) {
        return fileRepository.findById(id).orElseThrow(() -> new RuntimeException());
    }

    @Override
    public Stream<File> getAllFiles() {
        return fileRepository.findAll().stream();
    }
}

package com.share.io.service.file;

import com.share.io.dto.file.FileUploadDTO;
import com.share.io.model.file.File;
import com.share.io.model.file.FileType;
import com.share.io.model.user.User;
import com.share.io.repository.FileRepository;
import com.share.io.repository.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;
import java.util.stream.Stream;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private final FileRepository fileRepository;
    private final UserRepository userRepository;

    public FileStorageServiceImpl(FileRepository fileRepository, UserRepository userRepository) {
        this.fileRepository = fileRepository;
        this.userRepository = userRepository;
    }

    @Override
    public File save(MultipartFile fileData, Long uploaderId) {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(fileData.getOriginalFilename()));
        //TODO: Add custom exceptions
        User user = userRepository.findById(uploaderId).orElseThrow(() -> new RuntimeException());
        File file = new File();
        try {
            file.setOriginalName(fileName);
            file.setContentType(fileData.getContentType());
            file.setVersion(1L);
            file.setData(fileData.getBytes());
            file.setUploader(user);
            file.setExtension(StringUtils.getFilenameExtension(fileData.getOriginalFilename()));
            file.setFileType(FileType.getFileType(file.getExtension()));
        } catch (IOException e) {
            throw new RuntimeException();
        }

        return fileRepository.save(file);
    }

    @Override
    public File saveFileMetadata(String id, FileUploadDTO fileDTO, Long uploaderId) {
        File file = fileRepository.findById(id).orElseThrow(() -> new RuntimeException());
        file.setName(fileDTO.getName());
        file.setDescription(fileDTO.getDescription());
        return fileRepository.save(file);
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

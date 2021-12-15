package com.share.io.service.file;

import com.share.io.dto.file.FileUploadDTO;
import com.share.io.model.file.File;
import org.springframework.web.multipart.MultipartFile;

import java.util.stream.Stream;

public interface FileStorageService {

    File save(MultipartFile fileData, Long uploaderId);

    File saveFileMetadata(String id, FileUploadDTO fileDTO, Long uploaderId);

    File getFileById(String id);

    Stream<File> getAllFiles();
}

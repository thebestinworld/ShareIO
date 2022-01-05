package com.share.io.service.file;

import com.share.io.dto.file.FileUpdateDTO;
import com.share.io.dto.query.file.FileQuery;
import com.share.io.dto.file.FileUploadDTO;
import com.share.io.model.file.File;
import com.share.io.security.UserCurrent;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.stream.Stream;

public interface FileStorageService {

    File save(Long id, MultipartFile fileData, Long uploaderId);

    File saveFileMetadata(FileUploadDTO fileDTO, Long uploaderId);

    File update(Long id, MultipartFile fileData, UserCurrent userCurrent);

    File updateFileMetaData(Long id, FileUpdateDTO fileUpdateDTO, UserCurrent userCurrent);

    void deleteFile(Long id, UserCurrent userCurrent);

    void shareFile(Long id, Long userId, Long sharedToUserId, UserCurrent userCurrent);

    File getFileById(Long id);

    Stream<File> getAllFiles();

    Page<File> findAllFilesBySpecification(FileQuery query);
}

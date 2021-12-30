package com.share.io.service.file;

import com.share.io.dto.file.FileUpdateDTO;
import com.share.io.dto.query.file.FileQuery;
import com.share.io.dto.file.FileUploadDTO;
import com.share.io.model.file.File;
import com.share.io.security.UserCurrent;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.stream.Stream;

public interface FileStorageService {

    File save(String id, MultipartFile fileData, Long uploaderId);

    File saveFileMetadata(FileUploadDTO fileDTO, Long uploaderId);

    File update(String id, MultipartFile fileData, UserCurrent userCurrent);

    File updateFileMetaData(String id, FileUpdateDTO fileUpdateDTO, UserCurrent userCurrent);

    void deleteFile(String id, UserCurrent userCurrent);

    void shareFile(String id, Long userId, Long sharedToUserId, UserCurrent userCurrent);

    File getFileById(String id);

    Stream<File> getAllFiles();

    Collection<File> findAllFilesBySpecification(FileQuery query);
}

package com.share.io.dto.query.file;

import com.share.io.dto.query.BetweenQuery;
import com.share.io.model.file.FileType;

import java.time.LocalDateTime;

public class FileQuery {

    private Long userId;

    private String originalName;

    private String name;

    private String description;

    private FileType fileType;

    private String contentType;

    private String extension;

    private Long version;

    private BetweenQuery<LocalDateTime> uploadDate;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public FileType getFileType() {
        return fileType;
    }

    public void setFileType(FileType fileType) {
        this.fileType = fileType;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public BetweenQuery<LocalDateTime> getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(BetweenQuery<LocalDateTime> uploadDate) {
        this.uploadDate = uploadDate;
    }
}

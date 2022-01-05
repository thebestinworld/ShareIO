package com.share.io.model.file;

import org.springframework.util.ObjectUtils;

import java.util.List;

public enum FileType {
    AUDIO(List.of("wav", "mp3")),
    VIDEO(List.of("mp4", "webm")),
    IMAGE(List.of("png", "jpeg"));

    private final List<String> allowedExtensions;

    FileType(List<String> allowedExtensions) {
        this.allowedExtensions = allowedExtensions;
    }

    public static FileType getFileTypeFromExtension(String extension) {
        for (FileType value : FileType.values()) {
            if (value.allowedExtensions.contains(extension.toLowerCase())) {
                return value;
            }
        }
        throw new RuntimeException("Not allowed extension.");
    }

    public static FileType getFileType(String fileType) {
        if (ObjectUtils.isEmpty(fileType)) {
            return null;
        }
        return FileType.valueOf(fileType);
    }
}

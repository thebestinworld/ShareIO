package com.share.io.model.file;

import java.util.List;

public enum FileType {
    AUDIO(List.of("mp3")),
    VIDEO(List.of("wav")),
    TEXT(List.of("txt")),
    IMAGE(List.of("PNG"));

    private final List<String> allowedExtensions;

    FileType(List<String> allowedExtensions) {
        this.allowedExtensions = allowedExtensions;
    }

    public static FileType getFileType(String extension) {
        for (FileType value : FileType.values()) {
            if (value.allowedExtensions.contains(extension.toUpperCase())) {
                return value;
            }
        }
        throw new RuntimeException("Not allowed extension.");
    }
}

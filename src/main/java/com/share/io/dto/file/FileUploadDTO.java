package com.share.io.dto.file;

public class FileUploadDTO {

    private String description;

    private String name;

    public FileUploadDTO() {
    }

    public FileUploadDTO(String description, String name) {
        this.description = description;
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

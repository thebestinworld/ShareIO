package com.share.io.dto.file;

public class FileUpdateDTO {

    private String description;
    private String name;


    public FileUpdateDTO() {
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
}

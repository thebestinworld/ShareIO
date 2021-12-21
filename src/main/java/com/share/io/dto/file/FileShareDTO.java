package com.share.io.dto.file;

public class FileShareDTO {

    private Long userToShareId;

    public FileShareDTO() {
    }

    public FileShareDTO(Long userToShareId) {
        this.userToShareId = userToShareId;
    }

    public Long getUserToShareId() {
        return userToShareId;
    }

    public void setUserToShareId(Long userToShareId) {
        this.userToShareId = userToShareId;
    }
}

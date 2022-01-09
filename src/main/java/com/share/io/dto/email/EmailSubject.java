package com.share.io.dto.email;

public enum EmailSubject {

    FILE_UPDATE("File %d has been updated"),
    FILE_SHARE("File %d has been shared"),
    FILE_DELETE("File %d has been deleted");

    private final String subject;

    EmailSubject(String subject) {
        this.subject = subject;
    }

    public String getSubject() {
        return subject;
    }
}

package com.share.io.model.eventlog;

public enum Event {

    UPDATED("File Updated"),
    SHARED("File Shared"),
    REVERTED("File Reverted"),
    UPLOADED("File Uploaded");

    private final String message;

    Event(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}

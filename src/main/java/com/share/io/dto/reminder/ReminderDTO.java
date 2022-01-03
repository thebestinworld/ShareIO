package com.share.io.dto.reminder;

public class ReminderDTO {

    private String message;
    private String time;

    public ReminderDTO() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}

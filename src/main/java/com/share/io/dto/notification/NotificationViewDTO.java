package com.share.io.dto.notification;

import java.util.List;

public class NotificationViewDTO {

    List<NotificationDTO> items;
    Long totalCount;

    public NotificationViewDTO() {
    }

    public List<NotificationDTO> getItems() {
        return items;
    }

    public void setItems(List<NotificationDTO> items) {
        this.items = items;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }
}

package com.share.io.dto.reminder;

import java.util.List;

public class ReminderViewDTO {

    List<ReminderDTO> items;
    Long totalCount;

    public ReminderViewDTO() {
    }

    public List<ReminderDTO> getItems() {
        return items;
    }

    public void setItems(List<ReminderDTO> items) {
        this.items = items;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }
}

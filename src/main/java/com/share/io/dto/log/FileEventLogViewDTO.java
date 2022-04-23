package com.share.io.dto.log;

import java.util.List;

public class FileEventLogViewDTO {

    List<FileEventLogDTO> items;
    Long totalCount;

    public FileEventLogViewDTO() {
    }

    public List<FileEventLogDTO> getItems() {
        return items;
    }

    public void setItems(List<FileEventLogDTO> items) {
        this.items = items;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }
}

package com.share.io.dto.file;

import java.util.List;

public class FileViewDTO {

    List<FileDTO> items;
    Long totalCount;

    public FileViewDTO() {
    }

    public List<FileDTO> getItems() {
        return items;
    }

    public void setItems(List<FileDTO> items) {
        this.items = items;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }
}

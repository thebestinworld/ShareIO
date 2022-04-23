package com.share.io.service.log;

import com.share.io.dto.query.log.FileEventLogQuery;
import com.share.io.model.eventlog.Event;
import com.share.io.model.eventlog.FileEventLog;
import org.springframework.data.domain.Page;

public interface FileEventLogService {

    void logEvent(Long fileId, Event event, String userName);

    Page<FileEventLog> findAll(FileEventLogQuery fileEventLogQuery);
}

package com.share.io.repository.log;

import com.share.io.model.eventlog.FileEventLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface FileEventLogRepository extends
        JpaRepository<FileEventLog, Long>, JpaSpecificationExecutor<FileEventLog> {
}

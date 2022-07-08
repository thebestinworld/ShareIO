package com.share.io.service.log;


import static com.share.io.repository.log.FileEventLogSpecification.dynamicContentContains;
import static com.share.io.repository.log.FileEventLogSpecification.event;
import static com.share.io.repository.log.FileEventLogSpecification.fileIdEquals;
import static com.share.io.repository.log.FileEventLogSpecification.idEquals;
import static com.share.io.repository.log.FileEventLogSpecification.sort;
import static com.share.io.repository.log.FileEventLogSpecification.timestampContains;
import static com.share.io.repository.log.FileEventLogSpecification.userNameContains;
import com.share.io.dto.query.log.FileEventLogQuery;
import com.share.io.model.eventlog.Event;
import com.share.io.model.eventlog.FileEventLog;
import com.share.io.repository.log.FileEventLogRepository;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FileEventLogServiceImpl implements FileEventLogService {

    private final FileEventLogRepository eventLogRepository;

    @Autowired
    public FileEventLogServiceImpl(FileEventLogRepository eventLogRepository) {
        this.eventLogRepository = eventLogRepository;
    }

    @Async
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logEvent(Long fileId, Event event, String userName) {
        FileEventLog eventLog = new FileEventLog();
        eventLog.setFileId(fileId);
        eventLog.setTimestamp(LocalDateTime.now(ZoneOffset.UTC));
        eventLog.setEvent(event);
        eventLog.setUserName(userName);

        eventLogRepository.save(eventLog);
    }

    @Async
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logEvent(Long fileId, Event event, String userName, String dynamicContent) {
        FileEventLog eventLog = new FileEventLog();
        eventLog.setFileId(fileId);
        eventLog.setTimestamp(LocalDateTime.now(ZoneOffset.UTC));
        eventLog.setEvent(event);
        eventLog.setUserName(userName);
        eventLog.setDynamicContent(dynamicContent);
        eventLogRepository.save(eventLog);
    }

    @Override
    public Page<FileEventLog> findAll(FileEventLogQuery fileEventLogQuery) {
        Specification<FileEventLog> specification = fileIdEquals(fileEventLogQuery.getFileId())
                .and(dynamicContentContains(fileEventLogQuery.getDynamicContent()))
                .and(idEquals(fileEventLogQuery.getId()))
                .and(timestampContains(fileEventLogQuery.getTimestamp()))
                .and(event(fileEventLogQuery.getEventType()))
                .and(userNameContains(fileEventLogQuery.getUserName()))
                .and(sort(fileEventLogQuery.getSort(), fileEventLogQuery.getOrder()));

        return eventLogRepository.findAll(specification,
                PageRequest.of(fileEventLogQuery.getPage(), fileEventLogQuery.getSize()));
    }

    @Override
    @Transactional
    public void deleteEventLogsByFileId(Long fileId) {
        this.eventLogRepository.deleteByFileId(fileId);
    }
}

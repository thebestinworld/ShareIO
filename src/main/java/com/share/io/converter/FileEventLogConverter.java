package com.share.io.converter;

import com.share.io.dto.log.FileEventLogDTO;
import com.share.io.model.eventlog.FileEventLog;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class FileEventLogConverter {

    public List<FileEventLogDTO> convert(Collection<FileEventLog> fileEventLogs) {
        return fileEventLogs.stream().map(this::convert)
                .collect(Collectors.toList());
    }

    public FileEventLogDTO convert(FileEventLog fileEventLog) {
        FileEventLogDTO fileEventLogDTO = new FileEventLogDTO();
        fileEventLogDTO.setId(fileEventLog.getId());
        fileEventLogDTO.setDynamicContent(fileEventLog.getDynamicContent());
        String time = fileEventLog.getTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        fileEventLogDTO.setTimestamp(time);
        fileEventLogDTO.setUserName(fileEventLog.getUserName());
        fileEventLogDTO.setEvent(fileEventLog.getEvent().toString());
        fileEventLogDTO.setEventMessage(fileEventLog.getEvent().getMessage());
        return fileEventLogDTO;
    }
}

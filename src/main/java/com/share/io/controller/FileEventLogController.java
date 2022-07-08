package com.share.io.controller;

import com.share.io.converter.FileEventLogConverter;
import com.share.io.dto.log.FileEventLogViewDTO;
import com.share.io.dto.query.log.FileEventLogQuery;
import com.share.io.model.eventlog.FileEventLog;
import com.share.io.service.log.FileEventLogService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/event_log")
public class FileEventLogController {

    private final FileEventLogService eventLogService;
    private final FileEventLogConverter eventLogConverter;

    public FileEventLogController(FileEventLogService eventLogService, FileEventLogConverter eventLogConverter) {
        this.eventLogService = eventLogService;
        this.eventLogConverter = eventLogConverter;
    }

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<FileEventLogViewDTO> findAllFileEventLogs(@RequestBody FileEventLogQuery fileEventLogQuery) {
        Page<FileEventLog> result = eventLogService.findAll(fileEventLogQuery);
        FileEventLogViewDTO fileEventLogViewDTO = new FileEventLogViewDTO();
        fileEventLogViewDTO.setItems(eventLogConverter.convert(result.getContent()));
        fileEventLogViewDTO.setTotalCount(result.getTotalElements());
        return ResponseEntity.status(HttpStatus.OK).body(fileEventLogViewDTO);
    }
}

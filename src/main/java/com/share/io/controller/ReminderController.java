package com.share.io.controller;

import com.share.io.converter.ReminderConverter;
import com.share.io.dto.query.reminder.ReminderQuery;
import com.share.io.dto.reminder.ReminderDTO;
import com.share.io.dto.reminder.ReminderViewDTO;
import com.share.io.model.reminder.Reminder;
import com.share.io.security.CurrentUser;
import com.share.io.security.UserCurrent;
import com.share.io.service.reminder.ReminderService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/reminder")
public class ReminderController {

    private final ReminderService reminderService;
    private final ReminderConverter reminderConverter;

    public ReminderController(ReminderService reminderService, ReminderConverter reminderConverter) {
        this.reminderService = reminderService;
        this.reminderConverter = reminderConverter;
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Reminder> createReminder(@RequestBody ReminderDTO reminderDTO,
                                                   @CurrentUser UserCurrent userCurrent) {
        Reminder result = reminderService.createReminder(reminderDTO, userCurrent.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ReminderViewDTO> findAllReminders(@CurrentUser UserCurrent userCurrent,
                                                            @RequestBody ReminderQuery reminderQuery) {
        Page<Reminder> result = reminderService.findAll(userCurrent.getId(), reminderQuery);
        ReminderViewDTO reminderViewDTO = new ReminderViewDTO();
        reminderViewDTO.setItems(reminderConverter.convert(result.getContent()));
        reminderViewDTO.setTotalCount(result.getTotalElements());
        return ResponseEntity.status(HttpStatus.OK).body(reminderViewDTO);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<String> deleteReminder(@PathVariable Long id) {
        this.reminderService.deleteReminder(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Reminder delete successfully!");
    }
}

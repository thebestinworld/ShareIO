package com.share.io.service.reminder;

import com.share.io.dto.query.reminder.ReminderQuery;
import com.share.io.dto.reminder.ReminderDTO;
import com.share.io.model.reminder.Reminder;
import org.springframework.data.domain.Page;

public interface ReminderService {

    Reminder createReminder(ReminderDTO reminderDTO, Long userId);

    void deleteReminder(Long id);

    Page<Reminder> findAll(Long userId, ReminderQuery reminderQuery);

    void sendReminders();
}

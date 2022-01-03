package com.share.io.service.reminder;

import com.share.io.dto.reminder.ReminderDTO;
import com.share.io.model.reminder.Reminder;

import java.util.List;

public interface ReminderService {

    Reminder createReminder(ReminderDTO reminderDTO, Long userId);

    void deleteReminder(Long id);

    List<Reminder> findAll(Long userId);

    void deleteReminders(List<Reminder> reminders);

    void sendReminders();
}

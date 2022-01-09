package com.share.io.converter;

import com.share.io.dto.reminder.ReminderDTO;
import com.share.io.model.reminder.Reminder;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ReminderConverter {

    public List<ReminderDTO> convert(Collection<Reminder> reminders) {
        return reminders.stream().map(this::convert)
                .collect(Collectors.toList());
    }

    public ReminderDTO convert(Reminder reminder) {
        ReminderDTO reminderDTO = new ReminderDTO();
        reminderDTO.setId(reminder.getId());
        reminderDTO.setMessage(reminder.getMessage());
        String time = reminder.getTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        reminderDTO.setTime(time);
        String pastDue = reminder.isPastDue() ? "Yes" : "No";
        reminderDTO.setPastDue(pastDue);
        return reminderDTO;
    }
}

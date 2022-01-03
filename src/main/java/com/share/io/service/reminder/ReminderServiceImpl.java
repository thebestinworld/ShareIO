package com.share.io.service.reminder;

import com.share.io.dto.reminder.ReminderDTO;
import com.share.io.model.reminder.Reminder;
import com.share.io.repository.reminder.ReminderRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ReminderServiceImpl implements ReminderService {

    private final ReminderRepository reminderRepository;
    private final SimpMessagingTemplate template;

    public ReminderServiceImpl(ReminderRepository reminderRepository, SimpMessagingTemplate template) {
        this.reminderRepository = reminderRepository;
        this.template = template;
    }

    @Override
    public Reminder createReminder(ReminderDTO reminderDTO, Long userId) {
        Reminder reminder = new Reminder();
        reminder.setMessage(reminderDTO.getMessage());
        reminder.setUserId(userId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH");
        LocalDateTime dateTime = LocalDateTime.parse(reminderDTO.getTime(), formatter);
        reminder.setTime(dateTime);
        return this.reminderRepository.save(reminder);
    }

    @Override
    public void deleteReminder(Long id) {
        this.reminderRepository.deleteById(id);
    }

    @Override
    public List<Reminder> findAll(Long id) {
        return this.reminderRepository.findAllByUserId(id);
    }

    @Override
    public void deleteReminders(List<Reminder> reminders) {
        this.reminderRepository.deleteAll(reminders);
    }

    @Override
    @Scheduled(cron = "0 0 0/1 * * ?")
    @Transactional
    public void sendReminders() {
        LocalDateTime localDateTime = LocalDateTime.now();
        String query = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh"));
        List<Reminder> remindersToSend = this.reminderRepository.findAllByTime(query);
        for (Reminder reminder : remindersToSend) {
            reminder.setPastDue(true);
            template.convertAndSend("/topic/reminder", reminder);
        }
        this.reminderRepository.saveAll(remindersToSend);
    }
}

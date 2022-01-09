package com.share.io.service.reminder;

import com.share.io.dto.query.reminder.ReminderQuery;
import com.share.io.dto.reminder.ReminderDTO;
import com.share.io.model.reminder.Reminder;
import com.share.io.repository.reminder.ReminderRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.share.io.repository.reminder.ReminderSpecification.idEquals;
import static com.share.io.repository.reminder.ReminderSpecification.messageContains;
import static com.share.io.repository.reminder.ReminderSpecification.pastDueIs;
import static com.share.io.repository.reminder.ReminderSpecification.sort;
import static com.share.io.repository.reminder.ReminderSpecification.timeContains;
import static com.share.io.repository.reminder.ReminderSpecification.userIdEquals;

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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse(reminderDTO.getTime(), formatter);
        reminder.setTime(dateTime);
        return this.reminderRepository.save(reminder);
    }

    @Override
    public void deleteReminder(Long id) {
        this.reminderRepository.deleteById(id);
    }

    @Override
    public Page<Reminder> findAll(Long id, ReminderQuery reminderQuery) {
        Specification<Reminder> specification = userIdEquals(id)
                .and(messageContains(reminderQuery.getMessage()))
                .and(idEquals(reminderQuery.getId()))
                .and(timeContains(reminderQuery.getTime()))
                .and(pastDueIs(reminderQuery.getPastDue()))
                .and(sort(reminderQuery.getSort(), reminderQuery.getOrder()));

        return reminderRepository.findAll(specification,
                PageRequest.of(reminderQuery.getPage(), reminderQuery.getSize()));
    }

    /*Executes every minute*/
    @Override
    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void sendReminders() {
        LocalDateTime localDateTime = LocalDateTime.now();
        String query = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        List<Reminder> remindersToSend = this.reminderRepository.findAllByTime(query);
        for (Reminder reminder : remindersToSend) {
            reminder.setPastDue(true);
            template.convertAndSend("/topic/reminder", reminder);
        }
        this.reminderRepository.saveAll(remindersToSend);
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void doSomethingAfterStartup() {
        this.reminderRepository.updatePastDueReminders();
    }
}

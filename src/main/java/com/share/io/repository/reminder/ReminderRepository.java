package com.share.io.repository.reminder;

import com.share.io.model.reminder.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReminderRepository extends JpaRepository<Reminder, Long> {

    List<Reminder> findAllByUserId(Long userId);

    @Query(value = "SELECT * FROM reminder where DATE_FORMAT(time,'%Y-%m-%d %H') = ?1 and past_due = 0",
            nativeQuery = true)
    List<Reminder> findAllByTime(String time);
}

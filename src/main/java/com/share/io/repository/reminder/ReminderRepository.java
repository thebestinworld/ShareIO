package com.share.io.repository.reminder;

import com.share.io.model.reminder.Reminder;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ReminderRepository extends JpaRepository<Reminder, Long>, JpaSpecificationExecutor<Reminder> {

    @Query(value = "SELECT * FROM reminder where DATE_FORMAT(reminder.time,'%Y-%m-%d %H:%i') = ?1",
            nativeQuery = true)
    List<Reminder> findAllByTime(String time);

    @Modifying
    @Query(value = "UPDATE reminder set past_due = true where reminder.time <= NOW()", nativeQuery = true)
    void updatePastDueReminders();
}

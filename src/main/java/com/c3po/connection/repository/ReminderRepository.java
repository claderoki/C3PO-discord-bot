package com.c3po.connection.repository;

import com.c3po.connection.Repository;
import com.c3po.database.*;
import com.c3po.model.reminder.NewReminder;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("singleton")
public class ReminderRepository extends Repository {
    public synchronized void create(NewReminder reminder) {
        String query = """
            INSERT INTO `reminder`
            (
                `reminder`.`user_id`,
                `reminder`.`channel_id`,
                `reminder`.`message`,
                `reminder`.`due_date`
            )
            VALUES (?,?,?,?)
            """;
        execute(query,
            new LongParameter(reminder.userId()),
            new LongParameter(reminder.channelId()),
            new StringParameter(reminder.message()),
            new DateTimeParameter(reminder.dueDate())
        );
    }
}
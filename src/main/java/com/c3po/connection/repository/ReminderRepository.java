package com.c3po.connection.repository;

import com.c3po.connection.Repository;
import com.c3po.database.*;
import com.c3po.model.reminder.NewReminder;

import javax.sql.DataSource;

public class ReminderRepository extends Repository {
    protected static ReminderRepository DB;

    public static ReminderRepository db() {
        if (DB == null) {
            DB = new ReminderRepository(DataSourceLoader.instance());
        }
        return DB;
    }

    protected ReminderRepository(DataSource dataSource) {
        super(dataSource);
    }

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
package com.c3po.connection.repository;

import com.c3po.connection.Repository;
import com.c3po.database.DataSourceLoader;
import com.c3po.database.LongParameter;
import com.c3po.database.Result;
import com.c3po.database.StringParameter;
import com.c3po.model.streak.Streak;

import javax.sql.DataSource;

public class StreakRepository extends Repository {
    protected static StreakRepository DB;

    public static StreakRepository db() {
        if (DB == null) {
            DB = new StreakRepository(DataSourceLoader.instance());
        }
        return DB;
    }

    protected StreakRepository(DataSource dataSource) {
        super(dataSource);
    }

    public synchronized Streak get(long humanId, String key) {
        String query = """
            SELECT
                `streak`.`current`,
                DATEDIFF(UTC_DATE(), DATE(`streak`.`last_set`)) as `days_missed`
            FROM `streak`
            WHERE `streak`.`key` = ?
            AND `streak`.`human_id` = ?
            """;

        Result result = getOne(query, new StringParameter(key), new LongParameter(humanId));
        if (result == null) {
            return new Streak(0, 0L);
        }
        return new Streak(result.getInt("current"), result.getLong("days_missed"));
    }

    public synchronized void reset(long humanId, String key) {
        String query = """
            UPDATE `streak`
                SET
                    `streak`.`current` = 1,
                    `streak`.`last_set` = UTC_TIMESTAMP()
            WHERE
                `streak`.`key`      = ?
            AND
                `streak`.`human_id` = ?
            """;
        execute(query, new StringParameter(key), new LongParameter(humanId));
    }

    public synchronized void add(long humanId, String key) {
        String query = """
            INSERT INTO `streak` (`key`, `human_id`, `current`, `greatest`)
            VALUES (?, ?, 1, 1)
            ON DUPLICATE KEY UPDATE
            `current`  = `current` + 1,
                `last_set` = UTC_TIMESTAMP(),
            `greatest` = GREATEST(`greatest`, `current`)
            """;
        execute(query, new StringParameter(key), new LongParameter(humanId));
    }

}
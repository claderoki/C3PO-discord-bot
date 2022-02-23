package com.c3po.connection.repository;

import com.c3po.connection.Repository;
import com.c3po.database.DataSourceLoader;
import com.c3po.database.LongParameter;
import com.c3po.database.Result;
import com.c3po.database.StringParameter;
import com.c3po.helper.setting.SettingScopeTarget;
import com.c3po.model.Milkyway;

import javax.sql.DataSource;
import java.sql.SQLException;

public class MilkywayRepository extends Repository {
    protected static MilkywayRepository DB;

    public static MilkywayRepository db() {
        if (DB == null) {
            DB = new MilkywayRepository(DataSourceLoader.instance());
        }
        return DB;
    }

    protected MilkywayRepository(DataSource dataSource) {
        super(dataSource);
    }

    protected Integer getIdentifier() {
        return 0;
    }

    protected void createMilkyway(Milkyway milkyway) throws SQLException {
        String query = """
                INSERT INTO
                    (`guild_id`, `user_id`, `identifier`, `description`,
                    `name`, `status`, `purchase_type`,
                    `item_id`, `amount`, `days_pending`)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        update(query,
                new LongParameter(milkyway.getTarget().getGuildId()),
                new LongParameter(milkyway.getTarget().getUserId()),
                new LongParameter(getIdentifier()),
                new StringParameter(milkyway.getDescription()),
                new StringParameter(milkyway.getName()),
                new StringParameter(milkyway.getStatus().getType()),
                new LongParameter(milkyway.getItemId()),
                new LongParameter(milkyway.getAmount()),
                new LongParameter(milkyway.getDaysPending()),
                new LongParameter(milkyway.getAmount())
        );
    }

}

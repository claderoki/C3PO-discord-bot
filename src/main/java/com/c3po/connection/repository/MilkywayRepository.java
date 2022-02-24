package com.c3po.connection.repository;

import com.c3po.command.milkyway.MilkywayItem;
import com.c3po.connection.Repository;
import com.c3po.database.*;
import com.c3po.helper.setting.SettingScopeTarget;
import com.c3po.model.Milkyway;
import com.c3po.model.SimpleItem;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;

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

    private Integer getIncrementIdentifier() {
        return 0;
    }

    public void create(Milkyway milkyway) {
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
                new LongParameter(milkyway.getIdentifier()),
                new StringParameter(milkyway.getDescription()),
                new StringParameter(milkyway.getName()),
                new StringParameter(milkyway.getStatus().getType()),
                Parameter.from(milkyway.getItemId()),
                new LongParameter(milkyway.getAmount()),
                new LongParameter(milkyway.getDaysPending()),
                new LongParameter(milkyway.getAmount())
        );
    }

    public ArrayList<MilkywayItem> getAvailableItems() {
        ArrayList<MilkywayItem> items = new ArrayList<>();
        for(Result result: query("SELECT `item_id`,`days_worth` FROM `milkyway_item`")) {
            items.add(MilkywayItem.builder()
                .itemId(result.getInt("item_id"))
                .daysWorth(result.getInt("days_worth"))
                .build());
        }
        return items;

    }


}

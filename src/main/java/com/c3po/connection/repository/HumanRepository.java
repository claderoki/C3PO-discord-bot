package com.c3po.connection.repository;

import com.c3po.connection.Repository;
import com.c3po.database.*;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HumanRepository extends Repository {
    protected static HumanRepository DB;

    public static HumanRepository db() {
        if (DB == null) {
            DB = new HumanRepository(DataSourceLoader.instance());
        }
        return DB;
    }

    protected HumanRepository(DataSource dataSource) {
        super(dataSource);
    }

    public Integer getHumanId(Long userId) {
        Result result = getOne("SELECT `id` FROM `human` WHERE `user_id` = ?", new LongParameter(userId));

        if (result != null) {
            return result.getInt("id");
        }
        return null;
    }

    public void createHumanFor(Long userId) {
        execute("INSERT INTO `human` (`user_id`) VALUES (?)", new LongParameter(userId));
    }

    public Long getGold(Integer humanId) {
        Result result = getOne("SELECT `gold` FROM `human` WHERE `id` = ?", new LongParameter(humanId));
        return result.getLong("gold");
    }

    public synchronized void addItem(int itemId, int humanId, int amount) {
        String query = """
            INSERT INTO human_item (item_id, human_id, amount)
            VALUES (?, ?, ?)
            ON DUPLICATE KEY UPDATE amount = amount + ?
            """;
        execute(query,
            new IntParameter(itemId),
            new IntParameter(humanId),
            new IntParameter(amount)
        );
    }

    public synchronized void addItems(List<Integer> itemIds, int humanId) {
        //TODO: mapping
        for(int itemId: itemIds) {
            addItem(humanId, itemId, 1);
        }
    }

}

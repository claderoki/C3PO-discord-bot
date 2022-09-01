package com.c3po.connection.repository;

import com.c3po.connection.Repository;
import com.c3po.database.*;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HumanRepository extends Repository {

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
            ON DUPLICATE KEY UPDATE `amount` = `amount` + ?
            """;
        execute(query,
            new IntParameter(itemId),
            new IntParameter(humanId),
            new IntParameter(amount),
            new IntParameter(amount)
            );
    }

    public synchronized void addItems(List<Integer> itemIds, int humanId) {
        Map<Integer, Integer> itemMapping = new HashMap<>();
        for(Integer itemId: itemIds) {
            itemMapping.putIfAbsent(itemId, 0);
            itemMapping.computeIfPresent(itemId, (k,v)->v+1);
        }

        for(var entrySet: itemMapping.entrySet()) {
            addItem(entrySet.getKey(), humanId, entrySet.getValue());
        }
    }

    public void increaseGold(int humanId, int amount) {
        String query = "UPDATE `human` SET `gold` = `gold` + ? WHERE `id` = ?";
        execute(query, new IntParameter(amount), new IntParameter(humanId));
    }

    public void decreaseGold(int humanId, int amount) {
        increaseGold(humanId, -amount);
    }

}

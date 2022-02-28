package com.c3po.connection.repository;

import com.c3po.connection.Repository;
import com.c3po.database.*;
import com.c3po.helper.PlaceholderList;
import com.c3po.model.SimpleItem;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemRepository extends Repository {
    protected static ItemRepository DB;

    public static ItemRepository db() {
        if (DB == null) {
            DB = new ItemRepository(DataSourceLoader.instance());
        }
        return DB;
    }

    protected ItemRepository(DataSource dataSource) {
        super(dataSource);
    }

    public ArrayList<SimpleItem> getAllSimpleItems() {
        ArrayList<SimpleItem> items = new ArrayList<>();
        for(Result result: query("SELECT `item`.`id`, `item.name`,`item.code` FROM `item`")) {
            items.add(SimpleItem.builder()
                .code(result.getString("code"))
                .id(result.getInt("id"))
                .name(result.getString("name"))
                .build());
        }
        return items;
    }

    public Map<Integer, Integer> getItemAmounts(Integer humanId, Integer... itemIds) {
        Map<Integer, Integer> amounts = new HashMap<>();
        PlaceholderList placeholderList = PlaceholderList.fromArray(itemIds);
        placeholderList.addBefore(new LongParameter(humanId));

        String query = """
            SELECT
                `item_id`,
                `amount`
            FROM `human_item`
            WHERE `human_id` = ? AND `item_id` IN (%s)
            """.formatted(placeholderList.getQuestionMarks());

        List<Integer> missingItemIds = new ArrayList<>(List.of(itemIds));
        for(Result result: query(query, placeholderList.getParameters())) {
            Integer itemId = result.getInt("item_id");
            Integer amount = result.getInt("amount");
            amounts.put(itemId, amount);
            missingItemIds.remove(itemId);
        }

        for(Integer itemId: missingItemIds) {
            amounts.put(itemId, 0);
        }

        return amounts;
    }

}

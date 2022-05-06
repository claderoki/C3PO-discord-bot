package com.c3po.connection.repository;

import com.c3po.connection.Repository;
import com.c3po.database.*;
import com.c3po.helper.PlaceholderList;
import com.c3po.model.item.ItemCategory;
import org.json.JSONArray;

import javax.sql.DataSource;
import java.util.*;
import java.util.stream.Collectors;

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

    public Map<Integer, Integer> getItemAmounts(Integer humanId, List<Integer> itemIds) {
        return getItemAmounts(humanId, itemIds.toArray(Integer[]::new));
    }

    public Map<Integer, Integer> getItemAmounts(Integer humanId, Integer... itemIds) {
        Map<Integer, Integer> amounts = new HashMap<>();
        PlaceholderList placeholderList = PlaceholderList.of(itemIds);
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

    public void spendItem(int humanId, int itemId, int amount) {
        String query = """
            UPDATE `human_item` SET `amount` = `amount` - ?
            WHERE `human_item`.`human_id` = ? AND `human_item`.`item_id` = ?
        """;
        execute(query, new IntParameter(amount), new IntParameter(humanId), new IntParameter(itemId));
    }

    public void addItem(int humanId, int itemId, int amount) {
        spendItem(humanId, itemId, -amount);
    }

    public Map<Integer, ItemCategory> getAllCategories() {
        Map<Integer, ItemCategory> itemCategories = new HashMap<>();

        String query = """
            SELECT
                `item_category`.`id`,
                `item_category`.`name`,
                `item_category`.`parent_id`,
                GROUP_CONCAT(`item`.`id`) AS `item_ids`
            FROM `item_category`
            INNER JOIN `item` ON `item`.`category_id` = `item_category`.`id`
            GROUP BY `item_category`.`id`
            """;

        for(Result result: query(query)) {
            Integer parentId = result.optInt("parent_id");
            int id = result.getInt("id");
            String name = result.getString("name");
            ItemCategory parent = parentId == null ? null : itemCategories.get(parentId);
            Set<Integer> itemIds = Arrays.stream(result.getString("item_ids").split(","))
                .map(Integer::parseInt)
                .collect(Collectors.toSet());
            ItemCategory itemCategory = new ItemCategory(id, name, parent, new ArrayList<>(), itemIds);
            itemCategories.put(id, itemCategory);
        }

        for(var i: itemCategories.values()) {
            ItemCategory current = i.parent();
            ItemCategory previous = i;
            while (current != null) {
                current.addChild(previous);
                current = current.parent();
                previous = current;
            }
        }

        return itemCategories;
    }

}

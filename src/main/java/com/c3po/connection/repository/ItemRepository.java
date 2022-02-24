package com.c3po.connection.repository;

import com.c3po.connection.Repository;
import com.c3po.database.*;
import com.c3po.model.Milkyway;
import com.c3po.model.SimpleItem;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;

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

}

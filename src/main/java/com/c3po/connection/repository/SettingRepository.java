package com.c3po.connection.repository;

import com.c3po.connection.Repository;
import com.c3po.database.*;
import com.c3po.helper.DataType;
import com.c3po.helper.setting.*;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class SettingRepository extends Repository {
    protected static SettingRepository DB;

    public static SettingRepository db() {
        if (DB == null) {
            DB = new SettingRepository(DataSourceLoader.instance());
        }
        return DB;
    }

    protected SettingRepository(DataSource dataSource) {
        super(dataSource);
    }

    public HashMap<Integer, SettingValue> getSettingValues(SettingScopeTarget target, String category) throws SQLException {
        HashMap<Integer, SettingValue> values = new HashMap<>();
        ArrayList<Parameter> params = new ArrayList<>();
        params.add(new StringParameter(category));

        StringBuilder query = new StringBuilder("""
                SELECT
                    `setting_value`.`id`,
                    `setting_value`.`value`,
                    `setting_value`.`setting_id`
                FROM `setting_value`
                    WHERE `setting_value`.`setting_id` IN (SELECT `id` FROM `setting` WHERE `category` = ?)
                """);

        if (target.getGuildId() != null) {
            query.append(" AND `setting_value`.`guild_id` = ? ");
            params.add(new LongParameter(target.getGuildId()));
        }
        if (target.getUserId() != null) {
            query.append(" AND `setting_value`.`user_id` = ? ");
            params.add(new LongParameter(target.getUserId()));
        }

        for (Result result: this.query(query.toString(), params)) {
            values.put(result.getInt("setting_id"),
                    SettingValue.builder()
                            .value(result.getString("value"))
                            .id(result.getInt("id"))
                            .settingId(result.getInt("setting_id"))
                    .build());
        }
        return values;
    }

    public HashMap<String, Setting> getSettings(String category) throws SQLException {
        HashMap<String, Setting> settings = new HashMap<>();
        for (Result result: query("SELECT * FROM `setting` WHERE `setting`.`category` = ?", new StringParameter(category))) {
            settings.put(result.getString("key"),
                    Setting.builder()
                            .id(result.getInt("id"))
                            .category(result.getString("category"))
                            .defaultValue(result.getString("default_value"))
                            .example(result.getString("example"))
                            .key(result.getString("key"))
                            .prompt(result.getString("prompt"))
                            .scope(SettingScope.find(result.getString("scope")))
                            .type(DataType.find(result.getString("type")))
                            .exposed(result.getBool("exposed"))
                    .build());
        }

        return settings;
    }

}

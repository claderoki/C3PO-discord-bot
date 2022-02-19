package com.c3po.connection.repository;

import com.c3po.connection.Repository;
import com.c3po.database.*;
import com.c3po.helper.DataType;
import com.c3po.helper.setting.*;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;

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

    private void create(SettingValue settingValue) throws SQLException {
        String query = "INSERT INTO `setting_value` (`user_id`, `guild_id`, `value`, `setting_id`) VALUES (?, ?, ?, ?)";
        update(query,
                Parameter.from(settingValue.getTarget().getUserId()),
                Parameter.from(settingValue.getTarget().getGuildId()),
                new StringParameter(settingValue.getValue()),
                new LongParameter(settingValue.getSettingId())
        );
    }

    private void update(SettingValue settingValue) throws SQLException {
        if (!settingValue.changed()) {
            return;
        }
        String query = "UPDATE `setting_value` SET `setting_value`.`value` = ? WHERE `setting_value`.`id` = ?";
        update(query, new StringParameter(settingValue.getValue()), new LongParameter(settingValue.getId()));
    }

    public void save(SettingValue... settingValues) throws SQLException {
        for (SettingValue settingValue: settingValues) {
            if (settingValue.getId() == 0 || settingValue.getId() == null) {
                create(settingValue);
            } else {
                update(settingValue);
            }
        }
    }

    public HashMap<String, SettingValue> getHydratedSettingValues(SettingScopeTarget target, String category, Collection<String> keys) throws SQLException {
        return getHydratedSettingValues(target, category, keys.toArray(new String[0]));
    }

    private HashMap<String, SettingValue> getHydratedSettingValues(SettingScopeTarget target, String category, String... settingKeys) throws SQLException {
        HashMap<String, SettingValue> values = new HashMap<>();
        ArrayList<Parameter> params = new ArrayList<>();

        StringBuilder query = new StringBuilder("""
                SELECT
                    IFNULL(`setting_value`.`id`, 0) as `id`,
                    CASE WHEN `setting_value`.`id` IS NULL THEN `setting`.`default_value` ELSE `setting_value`.`value` END AS `value`,
                    `setting`.`id` AS `setting_id`,
                    `setting`.`type` AS `setting_type`,
                    `setting`.`key` AS `setting_key`
                FROM `setting`
                LEFT JOIN `setting_value` ON `setting_value`.`setting_id` = `setting`.`id`
                """);

        if (target.getGuildId() != null) {
            query.append(" AND `setting_value`.`guild_id` = ? ");
            params.add(new LongParameter(target.getGuildId()));
        }
        if (target.getUserId() != null) {
            query.append(" AND `setting_value`.`user_id` = ? ");
            params.add(new LongParameter(target.getUserId()));
        }

        query.append(" WHERE `setting`.`category` = ? ");
        params.add(new StringParameter(category));

        for (int i = 0; i < settingKeys.length; i++) {
            String key = settingKeys[i];
            if (i == 0) {
                query.append(" AND `setting`.`key` IN (");
            }

            if (i == settingKeys.length-1) {
                query.append("?) ");
            } else {
                query.append("?,");
            }
            params.add(new StringParameter(key));
        }

        query.append(" GROUP BY `setting`.`id`");

        for (Result result: this.query(query.toString(), params)) {
            int id = result.getInt("id");
            String key = result.getString("setting_key");

            SettingValue.SettingValueBuilder builder = SettingValue.builder()
                    .target(target)
                    .id(id)
                    .type(DataType.find(result.getString("setting_type")))
                    .key(key)
                    .settingId(result.getInt("setting_id"))
            ;
            if (id == 0) {
                builder.newValue(result.optString("value"));
            } else {
                builder.value(result.optString("value"));
            }

            values.put(key, builder.build());
        }
        return values;
    }

    public Optional<SettingValue> getHydratedSettingValue(SettingScopeTarget target, String category, String settingKey) throws SQLException {
        HashMap<String, SettingValue> values = getHydratedSettingValues(target, category, settingKey);
        return values.values().stream().findFirst();
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
                            .value(result.optString("value"))
                            .id(result.getInt("id"))
                            .settingId(result.getInt("setting_id"))
                    .build());
        }
        return values;
    }

    private Setting resultToSetting(Result result) {
        return Setting.builder()
                .id(result.getInt("id"))
                .category(result.getString("category"))
                .defaultValue(result.optString("default_value"))
                .key(result.getString("key"))
                .scope(SettingScope.find(result.getString("scope")))
                .type(DataType.find(result.getString("type")))
                .build();
    }

    public HashMap<String, Setting> getSettings(String category) throws SQLException {
        HashMap<String, Setting> settings = new HashMap<>();
        for (Result result: query("SELECT * FROM `setting` WHERE `setting`.`category` = ?", new StringParameter(category))) {
            settings.put(result.getString("key"), resultToSetting(result));
        }

        return settings;
    }

    public HashMap<String, HashMap<String, Setting>> getAllSettings() throws SQLException {
        HashMap<String, HashMap<String, Setting>> settings = new HashMap<>();
        for (Result result: query("SELECT * FROM `setting` ORDER BY `setting`.`category`")) {
            settings.computeIfAbsent(result.getString("category"), (c) -> new HashMap<>()).put(result.getString("key"), resultToSetting(result));
        }

        return settings;
    }

}

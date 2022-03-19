package com.c3po.connection.repository;

import com.c3po.connection.Repository;
import com.c3po.core.Scope;
import com.c3po.core.ScopeTarget;
import com.c3po.core.property.PropertyValue;
import com.c3po.database.*;
import com.c3po.helper.DataType;
import com.c3po.helper.PlaceholderList;
import com.c3po.core.setting.*;
import com.c3po.core.setting.validation.Condition;
import com.c3po.core.setting.validation.SettingValidation;
import com.c3po.core.setting.validation.ValueType;

import javax.sql.DataSource;
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

    private void create(PropertyValue propertyValue) {
        String query = "INSERT INTO `setting_value` (`user_id`, `guild_id`, `value`, `setting_id`) VALUES (?, ?, ?, ?)";
        execute(query,
            Parameter.from(propertyValue.getTarget().getUserId()),
            Parameter.from(propertyValue.getTarget().getGuildId()),
            new StringParameter(propertyValue.getValue()),
            new LongParameter(propertyValue.getParentId())
        );
    }

    private void update(PropertyValue propertyValue) {
        if (!propertyValue.changed()) {
            return;
        }
        String query = "UPDATE `setting_value` SET `setting_value`.`value` = ? WHERE `setting_value`.`id` = ?";
        execute(query, new StringParameter(propertyValue.getValue()), new LongParameter(propertyValue.getId()));
    }

    public void save(PropertyValue... propertyValues) {
        for (PropertyValue propertyValue: propertyValues) {
            if (propertyValue.getId() == 0 || propertyValue.getId() == null) {
                create(propertyValue);
            } else {
                update(propertyValue);
            }
        }
    }

    public HashMap<Integer, PropertyValue> getHydratedPropertyValues(ScopeTarget target, String category, Collection<Integer> ids) {
        return getHydratedPropertyValues(target, category, ids.toArray(new Integer[0]));
    }

    public HashMap<Integer, PropertyValue> getHydratedPropertyValues(ScopeTarget target, String category, Integer... settingIds) {
        HashMap<Integer, PropertyValue> values = new HashMap<>();
        ArrayList<Parameter> params = new ArrayList<>();

        StringBuilder query = new StringBuilder("""
                SELECT
                    IFNULL(`setting_value`.`id`, 0) as `id`,
                    (
                        CASE WHEN `setting_value`.`id` IS NULL
                            THEN `setting`.`default_value`
                            ELSE `setting_value`.`value`
                        END) AS `value`,
                    `setting`.`id` AS `setting_id`,
                    `setting`.`type` AS `setting_type`
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

        if (settingIds.length > 0) {
            PlaceholderList placeholderList = PlaceholderList.of(settingIds);
            query.append(" AND `setting`.`id` IN (%s)".formatted(placeholderList.getQuestionMarks()));
            params.addAll(placeholderList.getParameters());
        }

        query.append(" GROUP BY `setting`.`id`");

        for (Result result: this.query(query.toString(), params)) {
            int id = result.getInt("id");
            PropertyValue.PropertyValueBuilder builder = PropertyValue.builder()
                .target(target)
                .id(id)
                .type(DataType.valueOf(result.getString("setting_type")))
                .parentId(result.getInt("setting_id"))
                ;
            if (id == 0) {
                builder.newValue(result.optString("value"));
            } else {
                builder.value(result.optString("value"));
            }

            values.put(result.getInt("setting_id"), builder.build());
        }
        return values;
    }

    public Optional<PropertyValue> getHydratedPropertyValue(ScopeTarget target, String category, Integer settingId) {
        HashMap<Integer, PropertyValue> values = getHydratedPropertyValues(target, category, settingId);
        return values.values().stream().findFirst();
    }

    public HashMap<Integer, PropertyValue> getPropertyValues(ScopeTarget target, String category) {
        HashMap<Integer, PropertyValue> values = new HashMap<>();
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
                PropertyValue.builder()
                    .value(result.optString("value"))
                    .id(result.getInt("id"))
                    .parentId(result.getInt("setting_id"))
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
            .description(result.optString("description"))
            .scope(Scope.valueOf(result.getString("scope")))
            .type(DataType.valueOf(result.getString("type")))
            .build();
    }

    public HashMap<Integer, Setting> getSettings(String category) {
        HashMap<Integer, Setting> settings = new HashMap<>();
        for (Result result: query("SELECT * FROM `setting` WHERE `setting`.`category` = ?", new StringParameter(category))) {
            settings.put(result.getInt("id"), resultToSetting(result));
        }

        return settings;
    }

    public Setting getSetting(int id) {
        Result result = getOne("SELECT * FROM `setting` WHERE `setting`.`id` = ?", new LongParameter(id));
        return resultToSetting(result);
    }

    public HashMap<String, HashMap<String, Setting>> getAllSettings() {
        HashMap<String, HashMap<String, Setting>> settings = new HashMap<>();
        for (Result result: query("SELECT * FROM `setting` ORDER BY `setting`.`category`")) {
            settings.computeIfAbsent(result.getString("category"), (c) -> new HashMap<>())
                .put(result.getString("key"), resultToSetting(result));
        }

        return settings;
    }

    public HashMap<String, HashMap<String, Integer>> getSettingIdentifiers() {
        HashMap<String, HashMap<String, Integer>> identifiers = new HashMap<>();
        for (Result result: query("SELECT `id`, `key`, `category` FROM `setting`")) {
            identifiers.computeIfAbsent(result.getString("category"), (c) -> new HashMap<>())
                .put(result.getString("key"), result.getInt("id"));
        }

        return identifiers;
    }

    public HashMap<Integer, ArrayList<SettingValidation>> getValidations() {
        HashMap<Integer, ArrayList<SettingValidation>> validations = new HashMap<>();
        for (Result result: query("SELECT * FROM `setting_validation`")) {
            validations.computeIfAbsent(result.getInt("setting_id"), (c) -> new ArrayList<>())
                .add(SettingValidation.builder()
                    .condition(Condition.valueOf(result.getString("condition")))
                    .settingId(result.getInt("setting_id"))
                    .id(result.getInt("id"))
                    .value(result.optString("value"))
                    .valueType(ValueType.valueOf(result.getString("value_type")))
                    .build());
        }

        return validations;
    }

    public void delete(PropertyValue propertyValue) {
        String query = "DELETE FROM `setting_value` WHERE `setting_value`.`id` = ?";
        execute(query, new IntParameter(propertyValue.getId()));
    }
}

package com.c3po.connection.repository;

import com.c3po.connection.Repository;
import com.c3po.core.Scope;
import com.c3po.core.ScopeTarget;
import com.c3po.core.property.PropertyValue;
import com.c3po.database.*;
import com.c3po.helper.DataType;
import com.c3po.helper.PlaceholderList;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;

public class AttributeRepository extends Repository {
    protected static AttributeRepository DB;

    public static AttributeRepository db() {
        if (DB == null) {
            DB = new AttributeRepository(DataSourceLoader.instance());
        }
        return DB;
    }

    protected AttributeRepository(DataSource dataSource) {
        super(dataSource);
    }

    private void create(ScopeTarget target, int attributeId, String value) {
        String query = """
            INSERT INTO `attribute_value`
                (`user_id`, `guild_id`, `value`, `attribute_id`, `date_created`)
            VALUES (?, ?, ?, ?, UTC_TIMESTAMP())
        """;
        execute(query,
            Parameter.from(target.getUserId()),
            Parameter.from(target.getGuildId()),
            new StringParameter(value),
            new LongParameter(attributeId)
        );
    }

    private void create(PropertyValue propertyValue) {
        create(propertyValue.getTarget(), propertyValue.getParentId(), propertyValue.getValue());
    }

    private void update(int attributeValueId, String value) {
        if (attributeValueId == 0) {
            return;
        }
        String query = """
            UPDATE `attribute_value`
                SET `attribute_value`.`value` = ?,
                `date_updated` = UTC_TIMESTAMP()
            WHERE `attribute_value`.`id` = ?
            """;
        execute(query, new StringParameter(value), new IntParameter(attributeValueId));
    }

    private void update(PropertyValue propertyValue) {
        if (propertyValue.changed()) {
            update(propertyValue.getId(), propertyValue.getValue());

        }
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

    public HashMap<Integer, PropertyValue> getHydratedPropertyValues(ScopeTarget target, Collection<Integer> ids) {
        return getHydratedPropertyValues(target, ids.toArray(new Integer[0]));
    }

    public HashMap<Integer, PropertyValue> getHydratedPropertyValues(ScopeTarget target, Integer... attributeIds) {
        HashMap<Integer, PropertyValue> values = new HashMap<>();
        ArrayList<Parameter> params = new ArrayList<>();

        StringBuilder query = new StringBuilder("""
                SELECT
                    IFNULL(`attribute_value`.`id`, 0) as `id`,
                    (CASE WHEN `attribute_value`.`id` IS NULL
                        THEN `attribute`.`default_value`
                        ELSE `attribute_value`.`value`
                    END) AS `value`,
                    `attribute`.`id` AS `attribute_id`,
                    `attribute`.`type` AS `type`
                FROM `attribute`
                LEFT JOIN `attribute_value` ON `attribute_value`.`attribute_id` = `attribute`.`id`
                """);

        if (target.getGuildId() != null) {
            query.append(" AND `attribute_value`.`guild_id` = ? ");
            params.add(new LongParameter(target.getGuildId()));
        }
        if (target.getUserId() != null) {
            query.append(" AND `attribute_value`.`user_id` = ? ");
            params.add(new LongParameter(target.getUserId()));
        }

        if (attributeIds.length > 0) {
            PlaceholderList placeholderList = PlaceholderList.of(attributeIds);
            query.append(" WHERE `attribute`.`id` IN (%s)".formatted(placeholderList.getQuestionMarks()));
            params.addAll(placeholderList.getParameters());
        }

        query.append(" GROUP BY `attribute`.`id`");

        for (Result result: this.query(query.toString(), params)) {
            int id = result.getInt("id");
            PropertyValue.PropertyValueBuilder builder = PropertyValue.builder()
                .target(target)
                .id(id)
                .type(DataType.valueOf(result.getString("type")))
                .parentId(result.getInt("attribute_id"))
                ;
            if (id == 0) {
                builder.newValue(result.optString("value"));
            } else {
                builder.value(result.optString("value"));
            }

            values.put(result.getInt("attribute_id"), builder.build());
        }
        return values;
    }

    public Optional<PropertyValue> getHydratedPropertyValue(ScopeTarget target, Integer attributeIds) {
        HashMap<Integer, PropertyValue> values = getHydratedPropertyValues(target, attributeIds);
        return values.values().stream().findFirst();
    }

    public HashMap<Integer, PropertyValue> getPropertyValues(ScopeTarget target) {
        HashMap<Integer, PropertyValue> values = new HashMap<>();
        ArrayList<Parameter> params = new ArrayList<>();

        StringBuilder query = new StringBuilder("""
                SELECT
                    `attribute_value`.`id`,
                    `attribute_value`.`value`,
                    `attribute_value`.`attribute_id`
                FROM `attribute_value`
                """);

        if (target.getGuildId() != null) {
            query.append(" AND `attribute_value`.`guild_id` = ? ");
            params.add(new LongParameter(target.getGuildId()));
        }
        if (target.getUserId() != null) {
            query.append(" AND `attribute_value`.`user_id` = ? ");
            params.add(new LongParameter(target.getUserId()));
        }

        for (Result result: this.query(query.toString(), params)) {
            values.put(result.getInt("attribute_id"),
                PropertyValue.builder()
                    .value(result.optString("value"))
                    .id(result.getInt("id"))
                    .parentId(result.getInt("attribute_id"))
                    .build());
        }
        return values;
    }

    public HashMap<String, Integer> getAttributeIdentifiers() {
        HashMap<String, Integer> identifiers = new HashMap<>();
        for (Result result: query("SELECT `id`, `key` FROM `attribute`")) {
            identifiers.put(result.getString("key"), result.getInt("id"));
        }

        return identifiers;
    }

    public void delete(PropertyValue propertyValue) {
        String query = "DELETE FROM `attribute_value` WHERE `attribute_value`.`id` = ?";
        execute(query, new IntParameter(propertyValue.getId()));
    }

    public String getOldestValueFor(Long guildId, Integer attributeId) {
        String query = """
            SELECT `value` FROM `attribute_value`
            WHERE `attribute_value`.`guild_id` = ?
            AND`attribute_value`.`attribute_id` = ?
            ORDER BY date_created ASC
            LIMIT 1
            """;

        Result result = getOne(query, new LongParameter(guildId), new IntParameter(attributeId));
        if (result == null) {
            return null;
        }

        return result.getString("value");
    }

}
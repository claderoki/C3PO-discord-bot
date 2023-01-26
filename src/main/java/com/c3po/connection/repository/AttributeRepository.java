package com.c3po.connection.repository;

import com.c3po.connection.Repository;
import com.c3po.core.Scope;
import com.c3po.core.ScopeTarget;
import com.c3po.core.property.Attribute;
import com.c3po.core.property.AttributeCondition;
import com.c3po.core.property.PropertyValue;
import com.c3po.database.*;
import com.c3po.database.result.Result;
import com.c3po.helper.DataType;
import com.c3po.helper.PlaceholderList;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AttributeRepository extends Repository {

    private void create(ScopeTarget target, int attributeId, String value) {
        String query = """
            INSERT INTO `attribute_value`
                (`user_id`, `guild_id`, `value`, `attribute_id`, `date_created`)
            VALUES (?, ?, ?, ?, UTC_TIMESTAMP())
        """;
        execute(query,
            new LongParameter(target.getUserId(), true),
            new LongParameter(target.getGuildId(), true),
            new StringParameter(value),
            new IntParameter(attributeId)
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
            if (propertyValue.getId() == null || propertyValue.getId() == 0) {
                create(propertyValue);
            } else {
                update(propertyValue);
            }
        }
    }

    public void save(Collection<PropertyValue> propertyValues) {
        save(propertyValues.toArray(PropertyValue[]::new));
    }

    public HashMap<Integer, PropertyValue> getHydratedPropertyValues(ScopeTarget target, List<Integer> ids) {
        return getHydratedPropertyValues(target, ids.toArray(new Integer[0]));
    }

    public HashMap<Integer, PropertyValue> getHydratedPropertyValues(ScopeTarget target, Integer... attributeIds) {
        HashMap<Integer, PropertyValue> values = new HashMap<>();
        ArrayList<Parameter<?>> params = new ArrayList<>();

        StringBuilder query = new StringBuilder("""
                SELECT
                    IFNULL(`attribute_value`.`id`, 0) as `id`,
                    `attribute`.`key` as `key`,
                    (CASE WHEN `attribute_value`.`id` IS NULL
                        THEN `attribute`.`default_value`
                        ELSE `attribute_value`.`value`
                    END) AS `value`,
                    `attribute`.`id` AS `attribute_id`,
                    `attribute`.`type` AS `type`
                FROM `attribute`
                LEFT JOIN `attribute_value` ON `attribute_value`.`attribute_id` = `attribute`.`id`
                """);

        query.append(" AND `attribute_value`.`guild_id`");
        if (target.getGuildId() == null) {
            query.append(" IS NULL ");
        } else {
            params.add(new LongParameter(target.getGuildId()));
            query.append(" = ? ");
        }

        if (target.getUserId() != null) {
            query.append(" AND `attribute_value`.`user_id` = ? ");
            params.add(new LongParameter(target.getUserId()));
        }

        List<String> wheres = new ArrayList<>();

        if (attributeIds.length > 0) {
            PlaceholderList placeholderList = PlaceholderList.of(attributeIds);
            wheres.add(" `attribute`.`id` IN (%s)".formatted(placeholderList.getQuestionMarks()));
            params.addAll(placeholderList.getParameters());
        }

        wheres.add(" `scope` = ? ");
        query.append(" WHERE ").append(String.join(" AND ", wheres));

        params.add(new StringParameter(target.getScope().name()));

        query.append(" GROUP BY `attribute`.`id`");

        for (Result result: this.getMany(query.toString(), params)) {
            int id = result.getInt("id");
            PropertyValue.PropertyValueBuilder builder = PropertyValue.builder()
                .target(target)
                .id(id)
                .type(DataType.valueOf(result.getString("type")))
                .parentId(result.getInt("attribute_id"));
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
        ArrayList<Parameter<?>> params = new ArrayList<>();

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

        for (Result result: this.getMany(query.toString(), params)) {
            values.put(result.getInt("attribute_id"),
                PropertyValue.builder()
                    .value(result.optString("value"))
                    .id(result.getInt("id"))
                    .parentId(result.getInt("attribute_id"))
                    .build());
        }
        return values;
    }

    public Attribute getAttribute(int attributeId) {
        String query = "SELECT * FROM `attribute` WHERE `id` = ?";
        Result result = getOne(query, new IntParameter(attributeId));
        if (result == null) {
            return null;
        }
        return Attribute.builder()
            .id(result.getInt("id"))
            .key(result.getString("key"))
            .scope(Scope.valueOf(result.getString("scope")))
            .type(DataType.valueOf(result.getString("type")))
            .defaultValue(result.optString("default_value"))
            .build();
    }

    public HashMap<String, Integer> getAttributeIdentifiers() {
        HashMap<String, Integer> identifiers = new HashMap<>();
        for (Result result: getMany("SELECT `id`, `key` FROM `attribute`")) {
            identifiers.put(result.getString("key"), result.getInt("id"));
        }

        return identifiers;
    }

    public void delete(PropertyValue propertyValue) {
        String query = "DELETE FROM `attribute_value` WHERE `attribute_value`.`id` = ?";
        execute(query, new IntParameter(propertyValue.getId()));
    }

    public int purge(Long guildId, Set<Long> userIdsStillInGuild) {
        PlaceholderList placeholderList = PlaceholderList.of(userIdsStillInGuild.toArray());
        placeholderList.addBefore(new LongParameter(guildId));

        String query = """
            DELETE FROM `attribute_value` WHERE `guild_id` = ? AND `user_id` NOT IN (%s)
            AND `attribute_id` IN (SELECT `id` FROM `attribute` WHERE `purge_values` = 1)
            """.formatted(placeholderList.getQuestionMarks());
        return execute(query, placeholderList.getParameters().toArray(Parameter[]::new));
    }

    public Set<Long> getUserIdsHaving(Long guildId, int attributeId) {
        return getMany("SELECT `user_id` FROM `attribute_value` WHERE `attribute_id` = ? AND `guild_id` = ?",
            new IntParameter(attributeId),
            new LongParameter(guildId))
            .stream()
            .map(c -> c.getLong("user_id"))
            .collect(Collectors.toSet());
    }

    private String getConditionOperator(AttributeCondition condition) {
        return switch (condition) {
            case GTE -> ">=";
            case LTE -> "<=";
        };
    }

    public Map<Long, String> queryCondition(Long guildId, int attributeId, AttributeCondition condition, String value) {
        String operator = getConditionOperator(condition);
        String query = "SELECT `user_id`, `value` FROM `attribute_value` WHERE `value` %s ? AND `guild_id` = ? AND `attribute_id` = ?";
        return getMany(query.formatted(operator),
            new StringParameter(value),
            new LongParameter(guildId),
            new IntParameter(attributeId))
            .stream()
            .collect(Collectors.toMap(r -> r.getLong("user_id"), r -> r.getString("value")));
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

package com.c3po.connection.repository;

import com.c3po.command.pigeon.validation.PigeonValidation;
import com.c3po.command.pigeon.validation.PigeonValidationData;
import com.c3po.connection.Repository;
import com.c3po.database.*;
import com.c3po.model.pigeon.*;
import com.c3po.model.pigeon.stat.*;
import com.c3po.model.pigeon.stat.core.Stat;

import javax.sql.DataSource;
import java.util.*;
import java.util.stream.Collectors;

public class PigeonRepository extends Repository {
    protected static PigeonRepository DB;

    public static PigeonRepository db() {
        if (DB == null) {
            DB = new PigeonRepository(DataSourceLoader.instance());
        }
        return DB;
    }

    protected PigeonRepository(DataSource dataSource) {
        super(dataSource);
    }

    public synchronized Integer getActiveId(int humanId) {
        Result result = getOne("SELECT `id` FROM `pigeon` WHERE human_id = ? AND `condition` = 'active'", new IntParameter(humanId));
        if (result == null) {
            return null;
        }

        return result.getInt("id");
    }

    private synchronized LinkedHashMap<StatType, Stat> getStats(Result result) {
        LinkedHashMap<StatType, Stat> stats = new LinkedHashMap<>();

        long experience = result.getLongOr("experience", 0L);
        int health = result.getIntOr("health", 0);
        int happiness = result.getIntOr("happiness", 0);
        int cleanliness = result.getIntOr("cleanliness", 0);
        int food = result.getIntOr("food", 0);

        stats.put(StatType.EXPERIENCE, new PigeonExperience(experience));
        stats.put(StatType.HEALTH, new PigeonHealth(health));
        stats.put(StatType.HAPPINESS, new PigeonHappiness(happiness));
        stats.put(StatType.CLEANLINESS, new PigeonCleanliness(cleanliness));
        stats.put(StatType.FOOD, new PigeonFood(food));

        return stats;
    }

    private String toMin(Integer min, String value) {
        if (min == null) {
            return String.valueOf(value);
        }
        return "GREATEST(%s, %s)".formatted(value, min);
    }

    private String toMax(Integer max, String value) {
        if (max == null) {
            return String.valueOf(value);
        }
        return "LEAST(%s, %s)".formatted(value, max);
    }

    public synchronized void updateWinnings(int pigeonId, PigeonWinnings winnings) {
        HumanGold gold = (HumanGold) winnings.getStat(StatType.GOLD);
        PigeonHealth health = (PigeonHealth) winnings.getStat(StatType.HEALTH);

        StringBuilder query = new StringBuilder("UPDATE `pigeon` ");
        if (gold != null && gold.getValue() != 0) {
            query.append("INNER JOIN `human` ON `pigeon`.`human_id` = `human`.`id` ");
        }
        query.append(" SET ");
        if (health != null && health.getValue() < 0) {
            query.append("`pigeon`.`condition` = (CASE WHEN `pigeon`.`health` + ").append(health.getValue()).append(" <= 0 THEN 'dead' ELSE `pigeon`.`condition` END),");
        }
        query.append(winnings.getStats().values().stream()
            .filter(c -> c.getValue() != 0)
            .map(c -> {
            String column = c.getStatType().toString().toLowerCase();
            return "`%s` = %s".formatted(column,
                toMin(c.getMin(), toMax(c.getMax(), column + " + " + c.getValue()))
            );
        }).collect(Collectors.joining(",")));
        if (winnings.getGoldModifier() != null) {
            query.append(", `pigeon`.`gold_modifier` = `gold_modifier` + ").append(winnings.getGoldModifier());
        }

        query.append(" WHERE `pigeon`.`id` = ?");

        execute(query.toString(), new IntParameter(pigeonId));
    }

    public synchronized Pigeon getPigeon(int id) {
        String query = """
            SELECT
                `pigeon`.`id`,
                `pigeon`.`human_id`,
                `pigeon`.`name`,
                `pigeon`.`health`,
                `pigeon`.`happiness`,
                `pigeon`.`cleanliness`,
                `pigeon`.`experience`,
                `pigeon`.`food`,
                `pigeon`.`status`,
                `pigeon`.`condition`,
                `pigeon`.`gold_modifier`
            FROM
                `pigeon`
            WHERE `pigeon`.`id` = ?
            LIMIT 1
            """;

        Result result = getOne(query, new IntParameter(id));
        if (result == null) {
            return null;
        }

        return Pigeon.builder()
            .id(result.getInt("id"))
            .humanId(result.getInt("human_id"))
            .name(result.getString("name"))
            .stats(getStats(result))
            .status(PigeonStatus.valueOf(result.getString("status").toUpperCase()))
            .condition(PigeonCondition.valueOf(result.getString("condition").toUpperCase()))
            .goldModifier(result.getDouble("gold_modifier"))
            .build();
    }

    public void updateStatus(int pigeonId, PigeonStatus status) {
        execute("UPDATE `pigeon` SET `status` = ? WHERE `id` = ?",
            new StringParameter(status.name().toLowerCase()),
            new IntParameter(pigeonId)
        );
    }

    public synchronized Set<IdlePigeon> getIdlePigeons(long guildId) {
        String query = """
            SELECT
                `earthling`.`user_id` as `user_id`,
                `pigeon`.`id` as `pigeon_id`
            FROM
            `pigeon`
            INNER JOIN `earthling` ON `earthling`.`global_human_id` = `pigeon`.`human_id` AND `earthling`.`guild_id` = ?
            WHERE `pigeon`.`condition` = 'active'
            AND `pigeon`.`status` = 'idle'
            ORDER BY RAND()
            """;
        return query(query, new LongParameter(guildId))
            .stream()
            .map(r -> new IdlePigeon(r.getInt("pigeon_id"), r.getLong("user_id")))
            .collect(Collectors.toSet());
    }

    public synchronized PigeonValidationData getValidationData(PigeonValidation validation) {
        List<Parameter> parameters = new ArrayList<>();
        StringBuilder query = new StringBuilder("SELECT");
        query.append("(`human`.`gold` >= ?) AS `has_gold_needed`, ");
        parameters.add(new IntParameter(validation.getGoldNeeded()));
        query.append("(`pigeon`.`id`) AS `pigeon_id`, ");
        query.append("(`human`.`id`) AS `human_id`, ");

        if (validation.getRequiredPigeonStatus() != null) {
            query.append("(`pigeon`.`status` IS NOT NULL AND `pigeon`.`status` = ?) AS `has_required_status`, ");
            parameters.add(new StringParameter(validation.getRequiredPigeonStatus().toString()));
        }

        if (validation.isNeedsAvailablePvpAction()) {
            query.append("(`pigeon`.`last_used_pvp` IS NULL OR DATE_ADD(`pigeon`.`last_used_pvp`, INTERVAL 3 HOUR) <= UTC_TIMESTAMP()) AS `has_available_pvp_action`,");
        }
        query.append("(SELECT COUNT(*) > 0 from `pigeon` p WHERE `p`.`human_id` = `human`.`id` AND `p`.`condition` = 'dead' AND `p`.`death_notified` = 0) as should_notify_death,");
        query.append("(IFNULL(`pigeon`.`pvp`, 0)) AS `has_pvp_enabled` ");

        query.append(" FROM `human` ");
        query.append("LEFT JOIN pigeon ON `pigeon`.`human_id` = `human`.`id` AND `pigeon`.`condition` = 'active' ");
        query.append("WHERE `human`.`id` = ? LIMIT 1 ");
        parameters.add(new IntParameter(validation.getHumanId()));

        Result result = getOne(query.toString(), parameters);
        return PigeonValidationData.builder()
            .pigeonId(result.optInt("pigeon_id"))
            .humanId(result.optInt("human_id"))
            .hasAvailablePvpAction(result.getBoolOr("has_available_pvp_action", true))
            .hasGoldNeeded(result.getBool("has_gold_needed"))
            .hasRequiredStatus(result.getBoolOr("has_required_status", true))
            .shouldNotifyDeath(result.getBool("should_notify_death"))
            .hasPvpEnabled(result.getBool("has_pvp_enabled"))
            .build();
    }

    public void setDeathNotified(Integer id) {
        execute("UPDATE `pigeon` SET `death_notified` = 1 WHERE `id` = ?", new IntParameter(id));
    }

    public synchronized List<PigeonWinnings> getWinnings(int explorationId) {
        String query = "SELECT * FROM `exploration_winnings` WHERE `exploration_id` = ?";
        return query(query, new IntParameter(explorationId)).stream()
            .map(result -> {
                PigeonWinnings winnings = new PigeonWinnings();
                Integer itemId = result.optInt("item_id");
                if (itemId != null) {
                    winnings.addItemId(itemId);
                }
                LinkedHashMap<StatType, Stat> stats = new LinkedHashMap<>(result.getColumns().stream()
                    .map(c -> {
                        try {
                            StatType statType = StatType.valueOf(c.toUpperCase());
                            return StatFactory.create(statType, result.getInt(c));
                        } catch (Exception e) {
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toMap(Stat::getStatType, g -> g)));
                winnings.setStats(stats);
                return winnings;
            }).toList();
    }

    public void createPigeon(int humanId, String name) {
        String query = "INSERT INTO `pigeon` (`human_id`, `name`) VALUES (?, ?)";
        execute(query, new IntParameter(humanId), new StringParameter(name));
    }

    public void increasePoopCount(int pigeonId) {
        String query = "UPDATE `pigeon` SET `poop_victim_count` = `poop_victim_count` + 1 WHERE `id` = ?";
        execute(query, new IntParameter(pigeonId));
    }

    public void increasePoopedOnCount(int pigeonId) {
        String query = "UPDATE `pigeon` SET `pooped_on_count` = `pooped_on_count` + 1 WHERE `id` = ?";
        execute(query, new IntParameter(pigeonId));
    }

}

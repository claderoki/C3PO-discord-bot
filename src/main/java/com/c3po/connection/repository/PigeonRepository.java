package com.c3po.connection.repository;

import com.c3po.command.pigeon.PigeonValidation;
import com.c3po.command.pigeon.PigeonValidationResult;
import com.c3po.connection.Repository;
import com.c3po.database.*;
import com.c3po.errors.PublicException;
import com.c3po.model.pigeon.Pigeon;
import com.c3po.model.pigeon.PigeonCondition;
import com.c3po.model.pigeon.PigeonStatus;
import com.c3po.model.pigeon.stat.*;

import javax.sql.DataSource;
import java.util.*;

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

    public synchronized String getName(int id) {
        Result result = getOne("SELECT `name` FROM `pigeon` WHERE id = ?", new IntParameter(id));
        if (result == null) {
            return null;
        }

        return result.getString("name");
    }

    public synchronized Integer getActiveId(int humanId) {
        Result result = getOne("SELECT `id` FROM `pigeon` WHERE id = ? AND `condition` = 'active'", new IntParameter(humanId));
        if (result == null) {
            return null;
        }

        return result.getInt("id");
    }

    private synchronized LinkedHashMap<StatType, Stat> getStats(Result result) {
        LinkedHashMap<StatType, Stat> stats = new LinkedHashMap<>();

        long experience = result.getLongOr("experience", 0L);
        long health = result.getLongOr("health", 0L);
        long happiness = result.getLongOr("happiness", 0L);
        long cleanliness = result.getLongOr("cleanliness", 0L);
        long food = result.getLongOr("food", 0L);

        stats.put(StatType.EXPERIENCE, new PigeonExperience(experience));
        stats.put(StatType.HEALTH, new PigeonHealth(health));
        stats.put(StatType.HAPPINESS, new PigeonHappiness(happiness));
        stats.put(StatType.CLEANLINESS, new PigeonCleanliness(cleanliness));
        stats.put(StatType.FOOD, new PigeonFood(food));

        return stats;
    }

    public synchronized Pigeon getPigeon(int id) {
        String query = """
            SELECT
                `pigeon`.`name`,
                `pigeon`.`health`,
                `pigeon`.`happiness`,
                `pigeon`.`cleanliness`,
                `pigeon`.`experience`,
                `pigeon`.`food`,
                `pigeon`.`status`,
                `pigeon`.`condition`
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
            .stats(getStats(result))
            .name(result.getString("name"))
            .status(PigeonStatus.valueOf(result.getString("status").toUpperCase()))
            .condition(PigeonCondition.valueOf(result.getString("condition").toUpperCase()))
            .build();
    }

    public void updateStatus(int pigeonId, PigeonStatus status) {
        execute("UPDATE `pigeon` SET `status` = ? WHERE `id` = ?",
            new StringParameter(status.name().toLowerCase()),
            new IntParameter(pigeonId)
        );
    }

    public synchronized PigeonValidationResult getValidationResult(PigeonValidation validation) {
        List<Parameter> parameters = new ArrayList<>();
        StringBuilder query = new StringBuilder("SELECT");
        query.append("(`human`.`gold` >= ?) AS `has_gold_needed`, ");
        parameters.add(new IntParameter(validation.getGoldNeeded()));
        query.append("(`pigeon`.`id`) AS `pigeon_id`, ");

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
        return PigeonValidationResult.builder()
            .pigeonId(result.optInt("pigeon_id"))
            .hasAvailablePvpAction(result.getBoolOr("has_available_pvp_action", true))
            .hasGoldNeeded(result.getBool("has_gold_needed"))
            .shouldNotifyDeath(result.getBool("should_notify_death"))
            .hasPvpEnabled(result.getBool("has_pvp_enabled"))
            .build();
    }

    public void setDeathNotified(Integer id) {
        execute("UPDATE `pigeon` SET `death_notified` = 1 WHERE `id` = ?", new IntParameter(id));
    }
}

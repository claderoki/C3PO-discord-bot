package com.c3po.connection.repository;

import com.c3po.connection.Repository;
import com.c3po.database.*;
import com.c3po.model.exploration.*;
import com.c3po.model.pigeon.PigeonWinnings;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ExplorationRepository extends Repository {
    protected static ExplorationRepository DB;

    public static ExplorationRepository db() {
        if (DB == null) {
            DB = new ExplorationRepository(DataSourceLoader.instance());
        }
        return DB;
    }

    public ExplorationRepository(DataSource dataSource) {
        super(dataSource);
    }

    public void createExploration(int locationId, LocalDateTime arrivalDate, int pigeonId) {
        execute("INSERT INTO `exploration` (planet_location_id, start_date, arrival_date, finished, pigeon_id) VALUES (?, UTC_TIMESTAMP(), ?, 0, ?)",
            new IntParameter(locationId),
            new DateTimeParameter(arrivalDate),
            new IntParameter(pigeonId)
        );
    }

    public Exploration getExploration(int pigeonId) {
        String query = """
            SELECT
                `exploration`.`id`,
                `arrival_date`,
                `start_date`,
                `actions_remaining`,
                `total_actions`,
                `planet_location_id`
            FROM
                `pigeon`
            INNER JOIN `exploration` ON `exploration`.`pigeon_id` = `pigeon`.`id` AND `exploration`.`finished` = 0
            WHERE `pigeon`.`id` = ?
            """;

        Result result = getOne(query, new IntParameter(pigeonId));
        if (result == null) {
            return null;
        }

        return Exploration.builder()
            .id(result.getInt("id"))
            .arrivalDate(result.getDateTime("arrival_date"))
            .startDate(result.getDateTime("start_date"))
            .actionsRemaining(result.getInt("actions_remaining"))
            .totalActions(result.getInt("total_actions"))
            .locationId(result.getInt("planet_location_id"))
            .build();
    }

    public SimplePlanetLocation getRandomLocation() {
        String query = """
            SELECT
                `exploration_planet_location`.`id` as id,
                IFNULL(`exploration_planet_location`.`image_url`, `exploration_planet`.`image_url`) as image_url,
                90 as `travel_distance`
            FROM
                `exploration_planet_location`
            INNER JOIN `exploration_planet` ON `exploration_planet`.`id` = `exploration_planet_location`.`planet_id`
            WHERE
                `exploration_planet`.`id` != 1
            AND
                `exploration_planet_location`.`active` = 1
            ORDER BY RAND()
            LIMIT 1
            """;

        Result result = getOne(query);
        return SimplePlanetLocation.builder()
            .id(result.getInt("id"))
            .imageUrl(result.getString("image_url"))
            .travelDistance(result.getInt("travel_distance"))
            .build();
    }

    private Map<Integer, List<ExplorationScenario>> getAllScenarios() {
        String query = """
            SELECT
                `action_id`,
                `text`,
                `exploration_action_scenario_winnings`.*
            FROM `exploration_action_scenario`
            INNER JOIN `exploration_action_scenario_winnings` ON `exploration_action_scenario_winnings`.`id` = `exploration_action_scenario`.`scenario_winnings_id`
        """;
        Map<Integer, List<ExplorationScenario>> scenarios = new HashMap<>();
        for (Result result: query(query)) {
            ExplorationScenario scenario = ExplorationScenario.builder()
                .cleanliness(result.getInt("cleanliness"))
                .food(result.getInt("food"))
                .gold(result.getInt("gold"))
                .happiness(result.getInt("happiness"))
                .experience(result.getInt("experience"))
                .health(result.getInt("health"))
                .itemId(result.optInt("item_id"))
                .itemCategoryId(result.optInt("item_category_id"))
                .text(result.getString("text"))
                .build();
            scenarios.computeIfAbsent(result.getInt("action_id"), a -> new ArrayList<>()).add(scenario);
        }

        return scenarios;
    }

    private Map<Integer, List<ExplorationAction>> getAllActions() {
        Map<Integer, List<ExplorationScenario>> scenarios = getAllScenarios();
        String query = "SELECT `id`, `name`, `symbol`, `location_id` FROM `exploration_action`";
        Map<Integer, List<ExplorationAction>> actions = new HashMap<>();
        for (Result result: query(query)) {
            actions.computeIfAbsent(result.getInt("location_id"), a -> new ArrayList<>())
                .add(new ExplorationAction(
                    result.getInt("id"),
                    result.getString("name"),
                    result.getString("symbol"),
                    scenarios.get(result.getInt("id"))
            ));
        }

        return actions;
    }

    public Map<Integer, FullExplorationLocation> getAllLocations() {
        Map<Integer, List<ExplorationAction>> actions = getAllActions();
        Map<Integer, FullExplorationLocation> locations = new HashMap<>();
        String query = """
            SELECT
                `exploration_planet_location`.`id` AS `id`,
                `planet_id`,
                IFNULL(`exploration_planet_location`.`image_url`, `exploration_planet`.`image_url`) AS `image_url`,
                `exploration_planet_location`.`name` AS `name`,
                `exploration_planet`.`name` AS `planet_name`
            FROM `exploration_planet_location`
            INNER JOIN `exploration_planet` ON `exploration_planet`.`id` = `exploration_planet_location`.`planet_id`
        """;
        for(Result result: query(query)) {
            FullExplorationLocation location = new FullExplorationLocation(
                result.getInt("id"),
                result.getInt("planet_id"),
                result.getString("image_url"),
                result.getString("planet_name"),
                result.getString("name"),
                actions.get(result.getInt("id"))
            );
            locations.put(location.id(), location);
        }
        return locations;
    }

    public void createWinnings(int explorationId, int actionId, PigeonWinnings winnings) {
        String query = """
            INSERT INTO `exploration_winnings` (`exploration_id`, `exploration_action_id`, `item_id`, %s) VALUES (?, ?, ?, %s)
            """.formatted(
            winnings.getStats().values().stream().map(c -> c.getStatType().toString().toLowerCase()).collect(Collectors.joining(", ")),
            winnings.getStats().values().stream().map(c -> String.valueOf(c.getValue())).collect(Collectors.joining(", "))
        );

        execute(query,
            new IntParameter(explorationId),
            new IntParameter(actionId),
            Parameter.from(winnings.getItemIds().isEmpty() ? null : winnings.getItemIds().get(0))
        );
    }

    public void finish(int explorationId) {
        String query = """
            UPDATE `exploration`
            SET
                `exploration`.`end_date` = UTC_TIMESTAMP(),
                `exploration`.`finished` = 1,
                `exploration`.`actions_remaining` = 0
            WHERE `exploration`.`id` = ?
            """;
        execute(query, new IntParameter(explorationId));
    }

    public void updateActionsRemaining(int explorationId, int actionsRemaining) {
        execute("UPDATE `exploration` SET `actions_remaining` = ? WHERE `id` = ?", new IntParameter(actionsRemaining), new IntParameter(explorationId));
    }

}

package com.c3po.connection.repository;

import com.c3po.connection.Repository;
import com.c3po.database.DataSourceLoader;
import com.c3po.database.DateTimeParameter;
import com.c3po.database.IntParameter;
import com.c3po.database.Result;
import com.c3po.model.exploration.SimplePlanetLocation;

import javax.sql.DataSource;
import java.time.LocalDateTime;

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
}

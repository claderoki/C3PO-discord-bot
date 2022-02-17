package com.c3po.connection.repository;

import com.c3po.connection.Repository;
import com.c3po.database.DataSourceLoader;
import com.c3po.database.LongParameter;
import com.c3po.database.Result;
import com.c3po.model.GuildRewardsSettings;

import javax.sql.DataSource;
import java.sql.SQLException;

public class GuildRewardsRepository extends Repository {
    protected static GuildRewardsRepository DB;

    public static GuildRewardsRepository db() {
        if (DB == null) {
            DB = new GuildRewardsRepository(DataSourceLoader.instance());
        }
        return DB;
    }

    protected GuildRewardsRepository(DataSource dataSource) {
        super(dataSource);
    }

    public void test() throws SQLException {
        query("SELECT * FROM `guild_rewards_profile`");
    }

    public GuildRewardsSettings getSettings(long guildId) throws SQLException {
        Result r = getOne("SELECT * FROM `guild_rewards_settings` WHERE `guild_rewards_settings`.`guild_id` = ?", new LongParameter(guildId));
        if (r == null) {
            return null;
        }
        return GuildRewardsSettings.builder()
                .id(r.getInt("id"))
                .enabled(r.getBool("enabled"))
                .timeout(r.getDuration("id"))
                .minPointsPerMessage(r.getInt("min_points_per_message"))
                .maxPointsPerMessage(r.getInt("max_points_per_message"))
                .build();
    }

}

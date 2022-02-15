package com.c3po.database.repositories;

import com.c3po.database.DataSourceLoader;
import com.c3po.database.Repository;

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

    public void getSettings() throws SQLException {
        query("SELECT * FROM `guild_rewards_settings`");
    }

}

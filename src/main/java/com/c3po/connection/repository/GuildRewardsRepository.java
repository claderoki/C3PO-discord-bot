package com.c3po.connection.repository;

import com.c3po.connection.Repository;
import com.c3po.database.*;
import com.c3po.helper.setting.SettingScopeTarget;

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

    public Integer getProfileId(SettingScopeTarget target) throws SQLException {
        Result result = getOne("SELECT `id` FROM `guild_rewards_profile` WHERE `guild_id` = ? AND `user_id` = ?",
            new LongParameter(target.getGuildId()),
            new LongParameter(target.getUserId())
        );
        if (result == null) {
            return null;
        }
        return result.getInt("id");
    }

    public void createProfile(SettingScopeTarget target) throws SQLException {
        update("INSERT INTO `guild_rewards_profile` (`guild_id`, `user_id`) VALUES (?,?)",
                new LongParameter(target.getGuildId()),
                new LongParameter(target.getUserId())
        );
    }

    public void incrementPoints(Integer profileId, int amount) throws SQLException {
        update("UPDATE `guild_rewards_profile` SET `points` = `points` + ? WHERE `id` = ?",
            new LongParameter(amount),
            new LongParameter(profileId)
        );
    }

    public Integer getPoints(Integer profileId) throws SQLException {
        Result result = getOne("SELECT `points` FROM `guild_rewards_profile` WHERE `id` = ?",
            new LongParameter(profileId)
        );
        if (result == null) {
            return null;
        }
        return result.getInt("id");
    }

}

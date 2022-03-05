package com.c3po.connection.repository;

import com.c3po.connection.Repository;
import com.c3po.database.*;

import javax.sql.DataSource;

public class HumanRepository extends Repository {
    protected static HumanRepository DB;

    public static HumanRepository db() {
        if (DB == null) {
            DB = new HumanRepository(DataSourceLoader.instance());
        }
        return DB;
    }

    protected HumanRepository(DataSource dataSource) {
        super(dataSource);
    }

    public Integer getHumanId(Long userId) {
        Result result = getOne("SELECT `id` FROM `human` WHERE `user_id` = ?", new LongParameter(userId));

        if (result != null) {
            return result.getInt("id");
        }
        return null;
    }

    public void createHumanFor(Long userId) {
        update("INSERT INTO `human` (`user_id`) VALUES (?)", new LongParameter(userId));
    }

}

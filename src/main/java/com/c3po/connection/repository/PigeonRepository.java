package com.c3po.connection.repository;

import com.c3po.connection.Repository;
import com.c3po.database.DataSourceLoader;
import com.c3po.database.IntParameter;
import com.c3po.database.Result;

import javax.sql.DataSource;

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

    public synchronized String getName(int humanId) {
        Result result = getOne("SELECT `name` FROM `pigeon` WHERE human_id = ?", new IntParameter(humanId));
        if (result == null) {
            return null;
        }

        return result.getString("name");
    }

}

package com.c3po.connection.repository;

import com.c3po.connection.Repository;
import com.c3po.database.*;

import javax.sql.DataSource;

public class WordRepository extends Repository {
    protected static WordRepository DB;

    public static WordRepository db() {
        if (DB == null) {
            DB = new WordRepository(DataSourceLoader.instance());
        }
        return DB;
    }

    public WordRepository(DataSource dataSource) {
        super(dataSource);
    }

    public void createWord(String word, String description) {
        execute("INSERT INTO `word` (value, description) VALUES (?, ?)",
            new StringParameter(word),
            new StringParameter(description)
        );
    }
}

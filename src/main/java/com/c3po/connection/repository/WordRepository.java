package com.c3po.connection.repository;

import com.c3po.connection.Repository;
import com.c3po.database.*;
import com.c3po.model.exploration.Exploration;
import com.c3po.model.exploration.ExplorationAction;
import com.c3po.model.exploration.ExplorationScenario;
import com.c3po.model.exploration.FullExplorationLocation;
import com.c3po.model.pigeon.PigeonWinnings;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

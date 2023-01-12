package com.c3po.connection.repository;

import com.c3po.connection.Repository;
import com.c3po.connection.query.QueryBuilder;
import com.c3po.core.ScopeTarget;
import com.c3po.database.LongParameter;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CredentialRepository extends Repository {

    private void addTargetToQuery(QueryBuilder query, ScopeTarget target) {
        if (target.getGuildId() != null) {
            query.addWhere(" AND `guild_id` = ? ", new LongParameter(target.getGuildId()));
        }
        if (target.getUserId() != null) {
            query.addWhere(" AND `user_id` = ? ", new LongParameter(target.getUserId()));
        }
    }

    public synchronized Map<String, String> getCredentials(String category, ScopeTarget target) {
        QueryBuilder query = new QueryBuilder("SELECT `key`, `value` FROM `credential`");
        addTargetToQuery(query, target);

        return streamMany(query.build())
            .collect(Collectors.toMap(
                r -> r.getString("key"),
                r -> r.getString("value")
            ));
    }

}
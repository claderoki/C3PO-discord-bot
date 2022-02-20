package com.c3po.helper.setting;

import com.c3po.database.LongParameter;
import com.c3po.database.Parameter;

import java.util.Collection;

public class SettingScopeTargetHelper {
    public static void hydrateQueryWithConditions(SettingScopeTarget target, StringBuilder query, Collection<Parameter> parameters) {
        if (target.getGuildId() != null) {
            query.append(" AND `guild_id` = ?");
            parameters.add(new LongParameter(target.getGuildId()));
        }
        if (target.getUserId() != null) {
            query.append(" AND `user_id` = ?");
            parameters.add(new LongParameter(target.getUserId()));
        }
    }
}

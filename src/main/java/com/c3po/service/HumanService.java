package com.c3po.service;

import com.c3po.connection.repository.HumanRepository;
import com.c3po.helper.cache.Cache;
import com.c3po.helper.cache.keys.HumanIdKey;
import com.c3po.core.ScopeTarget;

public class HumanService extends Service {

    public static Integer getHumanId(long userId) {
        return Cache.computeIfAbsent(new HumanIdKey(ScopeTarget.user(userId)), (key) -> {
            Integer humanId = HumanRepository.db().getHumanId(userId);
            if (humanId == null) {
                HumanRepository.db().createHumanFor(userId);
                humanId = HumanRepository.db().getHumanId(userId);
                if (humanId == null) {
                    throw new RuntimeException("Something went wrong here creating human...");
                }
            }
            return humanId;
        });
    }
}

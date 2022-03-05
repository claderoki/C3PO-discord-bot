package com.c3po.service;

import com.c3po.connection.repository.HumanRepository;
import com.c3po.helper.cache.Cache;
import com.c3po.helper.cache.keys.HumanIdKey;
import com.c3po.helper.setting.SettingScopeTarget;

public class HumanService extends Service {

    public static Integer getHumanId(long userId) {
        HumanIdKey key = new HumanIdKey(SettingScopeTarget.user(userId));

        Integer humanId = Cache.get(key);
        if (humanId != null) {
            return humanId;
        }

        humanId = HumanRepository.db().getHumanId(userId);
        if (humanId == null) {
            HumanRepository.db().createHumanFor(userId);
            humanId = HumanRepository.db().getHumanId(userId);
            if (humanId == null) {
                throw new RuntimeException("Something went wrong here creating human...");
            }
        }
        Cache.set(key, humanId);
        return humanId;
    }

}

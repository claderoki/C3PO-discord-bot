package com.c3po.service;

import com.c3po.connection.repository.PigeonRepository;
import com.c3po.helper.cache.Cache;
import com.c3po.helper.cache.keys.PigeonNameKey;

public class PigeonService {

    public static String getName(int humanId) {
        PigeonNameKey key = new PigeonNameKey(humanId);
        String name = Cache.get(key);
        if (name != null) {
            return name;
        }

        name = PigeonRepository.db().getName(humanId);
        if (name != null) {
            Cache.set(key, name);
            return name;
        }

        return null;
    }
}

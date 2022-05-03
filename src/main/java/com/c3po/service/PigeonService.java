package com.c3po.service;

import com.c3po.connection.repository.PigeonRepository;
import com.c3po.helper.cache.Cache;
import com.c3po.helper.cache.keys.PigeonIdKey;
import com.c3po.model.pigeon.Pigeon;

public class PigeonService {
    public static Integer getCurrentId(int humanId) {
        return Cache.computeIfAbsent(new PigeonIdKey(humanId), (key) -> PigeonRepository.db().getActiveId(humanId));
    }

    public static Pigeon getPigeon(int id) {
        //TODO: when all pigeon functionality has been moved over, make this cache stats
        return PigeonRepository.db().getPigeon(id);
    }
}

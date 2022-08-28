package com.c3po.service;

import com.c3po.connection.repository.PigeonRepository;
import com.c3po.helper.cache.keys.PigeonIdKey;
import com.c3po.helper.cache.CacheManager;
import com.c3po.model.pigeon.Pigeon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PigeonService {

    @Autowired
    private PigeonRepository pigeonRepository;

    public Integer getCurrentId(int humanId) {
        return CacheManager.get().computeIfAbsent(new PigeonIdKey(humanId), (key) -> pigeonRepository.getActiveId(humanId));
    }

    public Pigeon getPigeon(int id) {
        //TODO: when all pigeon functionality has been moved over, make this cache stats
        return pigeonRepository.getPigeon(id);
    }
}

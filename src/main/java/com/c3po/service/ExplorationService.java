package com.c3po.service;

import com.c3po.connection.repository.ExplorationRepository;
import com.c3po.helper.cache.keys.ExplorationLocationsKey;
import com.c3po.helper.cache.CacheManager;
import com.c3po.model.exploration.FullExplorationLocation;

import java.util.Map;

public class ExplorationService extends Service {
    private final ExplorationRepository explorationRepository = ExplorationRepository.db();
    public Map<Integer, FullExplorationLocation> getAllLocations() {
        return CacheManager.get().computeIfAbsent(new ExplorationLocationsKey(), c -> explorationRepository.getAllLocations());
    }
}

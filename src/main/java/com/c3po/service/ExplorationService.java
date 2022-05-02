package com.c3po.service;

import com.c3po.connection.repository.ExplorationRepository;
import com.c3po.helper.cache.Cache;
import com.c3po.helper.cache.keys.ExplorationLocationsKey;
import com.c3po.model.exploration.FullExplorationLocation;

import java.util.Map;

public class ExplorationService extends Service {
    public static Map<Integer, FullExplorationLocation> getAllLocations() {
        return Cache.computeIfAbsent(new ExplorationLocationsKey(), c -> ExplorationRepository.db().getAllLocations());
    }
}

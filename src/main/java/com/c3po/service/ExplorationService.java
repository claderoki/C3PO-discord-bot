package com.c3po.service;

import com.c3po.connection.repository.ExplorationRepository;
import com.c3po.helper.cache.CacheManager;
import com.c3po.helper.cache.keys.ExplorationLocationsKey;
import com.c3po.model.exploration.FullExplorationLocation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ExplorationService {
    private final ExplorationRepository explorationRepository;

    public Map<Integer, FullExplorationLocation> getAllLocations() {
        return CacheManager.get().computeIfAbsent(new ExplorationLocationsKey(), c -> explorationRepository.getAllLocations());
    }
}

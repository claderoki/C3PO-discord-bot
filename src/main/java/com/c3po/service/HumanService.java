package com.c3po.service;

import com.c3po.connection.repository.HumanRepository;
import com.c3po.core.ScopeTarget;
import com.c3po.helper.cache.CacheManager;
import com.c3po.helper.cache.keys.HumanIdKey;
import discord4j.common.util.Snowflake;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HumanService {
    private final HumanRepository humanRepository;

    public Integer getHumanId(long userId) {
        return CacheManager.get().computeIfAbsent(new HumanIdKey(ScopeTarget.user(userId)), (key) -> {
            Integer humanId = humanRepository.getHumanId(userId);
            if (humanId == null) {
                humanRepository.createHumanFor(userId);
                humanId = humanRepository.getHumanId(userId);
                if (humanId == null) {
                    throw new RuntimeException("Something went wrong here creating human...");
                }
            }
            return humanId;
        });
    }

    public Integer getHumanId(Snowflake userId) {
        return getHumanId(userId.asLong());
    }

}

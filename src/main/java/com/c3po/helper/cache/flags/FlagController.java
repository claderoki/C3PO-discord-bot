package com.c3po.helper.cache.flags;

import com.c3po.helper.DateTimeHelper;
import com.c3po.helper.cache.CacheManager;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class FlagController {
    private final Flag flag;

    public boolean validate() {
        LocalDateTime lastSet = CacheManager.get("flags").get(flag);
        return lastSet == null;
    }

    public void spend() {
        CacheManager.get("flags").set(flag, DateTimeHelper.now());
    }
}

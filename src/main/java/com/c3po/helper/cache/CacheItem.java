package com.c3po.helper.cache;

import com.c3po.helper.DateTimeHelper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.OffsetDateTime;

@RequiredArgsConstructor
@Getter
public class CacheItem<T> {
    private final T value;
    private final OffsetDateTime expirationDate;

    public boolean isExpired() {
        return expirationDate != null && DateTimeHelper.offsetNow().isAfter(expirationDate);
    }
}

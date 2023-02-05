package com.c3po.helper;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.TemporalAmount;

public class TimedTrigger {
    private final TemporalAmount triggerDelay;

    private OffsetDateTime lastRefresh;

    public TimedTrigger(TemporalAmount triggerDelay) {
        this(triggerDelay, false);
    }

    public TimedTrigger(TemporalAmount triggerDelay, boolean skipInitialRun) {
        this.triggerDelay = triggerDelay;
        if (skipInitialRun) {
            refresh();
        }
    }

    private void refresh() {
        lastRefresh = OffsetDateTime.now(ZoneOffset.UTC);
    }

    public synchronized void check(Runnable onTrigger) {
        if (lastRefresh == null || OffsetDateTime.now(ZoneOffset.UTC).isAfter(lastRefresh.plus(triggerDelay))) {
            onTrigger.run();
            refresh();
        }
    }
}

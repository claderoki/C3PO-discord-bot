package com.c3po.command.milkyway;

import com.c3po.helper.DateTimeHelper;
import com.c3po.model.milkyway.Milkyway;

import java.time.LocalDateTime;

public class MilkywayHelper {
    public static String getChannelDescriptionFor(Milkyway milkyway, LocalDateTime expiresAt) {
        return milkyway.getDescription() + ", expires at " + expiresAt.format(DateTimeHelper.DATETIME_FORMATTER);
    }
}

package com.c3po.connection.repository;

import com.c3po.connection.Repository;
import com.c3po.database.*;
import com.c3po.helper.PlaceholderList;
import com.c3po.core.ScopeTarget;
import com.c3po.model.milkyway.ExpiredMilkyway;
import com.c3po.model.milkyway.Milkyway;
import com.c3po.model.milkyway.MilkywayStatus;
import com.c3po.model.milkyway.PurchaseType;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MilkywayRepository extends Repository {
    protected static MilkywayRepository DB;

    public static MilkywayRepository db() {
        if (DB == null) {
            DB = new MilkywayRepository(DataSourceLoader.instance());
        }
        return DB;
    }

    protected MilkywayRepository(DataSource dataSource) {
        super(dataSource);
    }

    public Long getIncrementIdentifier(long guildId) {
        Result result = getOne("SELECT MAX(`identifier`) as `identifier` FROM `milkyway` WHERE `guild_id` = ?", new LongParameter(guildId));
        if (result == null) {
            return 1L;
        }
        return result.getLongOr("identifier", 0L)+1;
    }

    public void create(Milkyway milkyway) {
        String query = """
                INSERT INTO `milkyway`
                    (`guild_id`, `user_id`, `identifier`,`description`,
                    `name`, `status`, `purchase_type`, `item_id`, `amount`, `days_pending`)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        execute(query,
            new LongParameter(milkyway.getTarget().getGuildId()),
            new LongParameter(milkyway.getTarget().getUserId()),
            new LongParameter(milkyway.getIdentifier()),
            new StringParameter(milkyway.getDescription()),
            new StringParameter(milkyway.getName()),
            new StringParameter(milkyway.getStatus().toString()),
            new StringParameter(milkyway.getPurchaseType().toString()),
            Parameter.from(milkyway.getItemId()),
            new LongParameter(milkyway.getAmount()),
            new LongParameter(milkyway.getDaysPending())
        );
    }

    public Milkyway get(long guildId, long identifier) {
        String query = "SELECT * FROM `milkyway` WHERE `guild_id` = ? AND `identifier` = ?";
        Result result = getOne(query, new LongParameter(guildId), new LongParameter(identifier));
        if (result == null) {
            return null;
        }
        return Milkyway.builder()
            .id(result.getInt("id"))
            .name(result.getString("name"))
            .description(result.getString("description"))
            .itemId(result.optInt("item_id"))
            .target(ScopeTarget.member(result.getLong("user_id"), result.getLong("guild_id")))
            .status(MilkywayStatus.valueOf(result.getString("status").toUpperCase()))
            .identifier(identifier)
            .daysPending(result.optInt("days_pending"))
            .purchaseType(PurchaseType.valueOf(result.getString("purchase_type")))
            .amount(result.getInt("amount"))
            .channelId(result.optLong("channel_id"))
            .denyReason(result.optString("deny_reason"))
            .expiresAt(result.optDateTime("expires_at"))
            .totalDays(result.optInt("total_days"))
            .build();
    }

    public void accept(long guildId, long identifier, long channelId, LocalDateTime expiresAt) {
        String query = """
            UPDATE `milkyway` SET
                `status` = 'accepted',
                `expires_at` = ?,
                `channel_id` = ?,
                `total_days` = `days_pending`,
                `days_pending` = 0
            WHERE
                `guild_id` = ?
            AND
                `identifier` = ?
        """;
        execute(query,
            new DateTimeParameter(expiresAt),
            new LongParameter(channelId),
            new LongParameter(guildId),
            new LongParameter(identifier)
        );
    }

    public void deny(long guildId, long identifier, String reason) {
        String query = """
            UPDATE `milkyway` SET status = 'denied', `deny_reason` = ?
            WHERE `guild_id` = ?
            AND `identifier` = ?
        """;
        execute(query,
            new StringParameter(reason),
            new LongParameter(guildId),
            new LongParameter(identifier)
        );
    }

    public List<ExpiredMilkyway> getExpiredMilkyways() {
        String query = "SELECT `id`, `channel_id` FROM `milkyway` WHERE `status` = 'accepted' AND `expires_at` > UTC_TIMESTAMP()";
        List<ExpiredMilkyway> milkyways = new ArrayList<>();
        for( Result result: query(query)) {
            milkyways.add(ExpiredMilkyway.builder()
                .channelId(result.getLong("channel_id"))
                .id(result.getInt("id"))
                .build());
        }
        return milkyways;
    }

    public void expire(List<Integer> ids) {
        if (ids.isEmpty()) {
            return;
        }

        PlaceholderList placeholderList = PlaceholderList.of(ids.toArray());
        String query = """
            UPDATE `milkyway` SET status = 'expired'
            WHERE `id` IN (%s)
        """.formatted(placeholderList.getQuestionMarks());
        execute(query, placeholderList.getParameters().toArray(Parameter[]::new));
    }

}

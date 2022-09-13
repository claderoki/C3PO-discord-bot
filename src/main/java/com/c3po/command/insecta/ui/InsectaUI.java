package com.c3po.command.insecta.ui;

import com.c3po.command.insecta.core.Insecta;
import com.c3po.command.insecta.core.InsectaProfile;
import com.c3po.command.insecta.core.InsectaWinnings;
import com.c3po.helper.DateTimeDelta;
import com.c3po.helper.DateTimeHelper;
import com.c3po.helper.EmbedHelper;
import com.c3po.ui.input.base.Interactor;
import discord4j.core.spec.EmbedCreateFields;
import discord4j.core.spec.EmbedCreateSpec;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@RequiredArgsConstructor
public class InsectaUI {
    private final Interactor interactor;

    public Mono<Void> sendCollectOverview(InsectaWinnings winnings, InsectaProfile profile, LocalDateTime firstCollected) {
        long seconds = ChronoUnit.SECONDS.between(firstCollected, DateTimeHelper.now());
        DateTimeDelta delta = DateTimeDelta.fromSeconds(seconds);

        long totalGained = 0L;

        EmbedCreateSpec.Builder embed = EmbedHelper.base();
        embed.description("After %s, you get:".formatted(delta.format()));
        for(var winning: winnings.getValues().entrySet()) {
            Insecta insecta = winning.getKey();
            String value = "+" + winning.getValue();
            String name = "%s (%s)".formatted(insecta.getKey(), profile.getInsectarium().getCount(insecta));
            embed.addField(EmbedCreateFields.Field.of(name, value, false));
            totalGained += winning.getValue();
        }

        embed.footer(EmbedCreateFields.Footer.of("total: " + totalGained, null));
        return interactor.reply().withEmbeds(embed.build());
    }

    public Mono<Void> sendPurchaseView(InsectaProfile profile, Insecta insecta, Long amount) {
        long currentCount = profile.getInsectarium().getCount(insecta);
        long cost = (insecta.getCost()*amount);
        var embed = EmbedHelper.base();
        if (currentCount == amount && insecta.getDidYouKnow() != null) {
            embed.footer(insecta.getDidYouKnow(), null);
        }

        embed.description("You just purchased %s %s's for %s hexacoins".formatted(amount, insecta.getKey(), cost));
        return interactor.reply().withEmbeds(embed.build());
    }
}

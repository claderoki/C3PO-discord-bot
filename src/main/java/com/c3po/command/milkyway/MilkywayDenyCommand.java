package com.c3po.command.milkyway;

import com.c3po.command.Command;
import com.c3po.command.option.OptionContainer;
import com.c3po.connection.repository.GuildRewardsRepository;
import com.c3po.connection.repository.ItemRepository;
import com.c3po.connection.repository.MilkywayRepository;
import com.c3po.errors.PublicException;
import com.c3po.model.milkyway.Milkyway;
import com.c3po.model.milkyway.MilkywayStatus;
import com.c3po.service.GuildRewardService;
import com.c3po.service.HumanService;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import reactor.core.publisher.Mono;

public class MilkywayDenyCommand extends Command {
    @Override
    public String getName() {
        return "milkyway deny";
    }

    private void givebackPayment(Milkyway milkyway) {
        switch (milkyway.getPurchaseType()) {
            case POINT -> GuildRewardsRepository.db().incrementPoints(GuildRewardService.getProfileId(milkyway.getTarget()), milkyway.getAmount());
            case ITEM -> ItemRepository.db().addItem(HumanService.getHumanId(milkyway.getTarget().getUserId()), milkyway.getItemId(), milkyway.getAmount());
        }
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event, OptionContainer options) throws RuntimeException {
        long identifier = options.getLong("id");
        String reason = options.getString("reason");

        long guildId = event.getInteraction().getGuildId().orElseThrow().asLong();
        Milkyway milkyway = MilkywayRepository.db().get(guildId, identifier);
        if (milkyway.getId() == 0) {
            throw new PublicException("This milkyway does not exist.");
        } else if (!milkyway.getStatus().equals(MilkywayStatus.PENDING)) {
            throw new PublicException("This milkyway can't be denied anymore.");
        }

        MilkywayRepository.db().deny(guildId, identifier, reason);
        givebackPayment(milkyway);

        event.getInteraction().getUser().getPrivateChannel().subscribe((c) -> c.createMessage(
            "Your milkyway request has been denied, reason: " + reason
        ).then());

        return event.reply().withContent("OK.").then();
    }
}

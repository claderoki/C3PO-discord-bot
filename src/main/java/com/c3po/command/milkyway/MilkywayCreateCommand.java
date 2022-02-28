package com.c3po.command.milkyway;

import com.c3po.command.Command;
import com.c3po.connection.repository.ItemRepository;
import com.c3po.errors.PublicException;
import com.c3po.helper.setting.SettingScopeTarget;
import com.c3po.model.MilkywaySettings;
import com.c3po.model.PurchaseType;
import com.c3po.service.MilkywayService;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MilkywayCreateCommand extends Command {
    @Override
    public String getName() {
        return "milkyway create";
    }

    public void validate(MilkywaySettings settings) throws PublicException {
        if (!settings.isEnabled()) {
            throw new PublicException("This server has to have milkyways enabled first. Ask an admin to enable it first.");
        }
        if (settings.getCategoryId() == null) {
            throw new PublicException("This server has to have a milkyway category set first. Ask an admin to set it first.");
        }
        if (settings.getLogChannelId() == null) {
            throw new PublicException("This server has to have a milkyway log channel set first. Ask an admin to set it first.");
        }
    }

    public List<AvailablePurchase> getAvailablePurchases() {
        List<AvailablePurchase> availablePurchases = new ArrayList<>();

        List<MilkywayItem> items = MilkywayService.getItems();

        //TODO: humanId.
        Map<Integer, Integer> itemAmounts = ItemRepository.db().getItemAmounts(1,
            items.stream().map(MilkywayItem::getItemId).toArray(Integer[]::new));

        for(MilkywayItem item: items) {
            Integer amount = itemAmounts.get(item.getItemId());
            if (amount > 0) {
                availablePurchases.add(AvailablePurchase.builder()
                    .amount(amount)
                    .daysWorth(item.getDaysWorth() * amount)
                    .purchaseType(PurchaseType.ITEM)
                    .build());
            }
        }

        return availablePurchases;
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) throws Exception {
        SettingScopeTarget target = SettingScopeTarget.guild(event.getInteraction().getGuildId().orElseThrow().asLong());
//        GuildRewardsSettings rewardsSettings = GuildRewardService.getSettings(target);
        MilkywaySettings milkywaySettings = MilkywayService.getSettings(target);
        validate(milkywaySettings);
        getAvailablePurchases();

        /*
            1. Check what purchase options are available. and (optionally) let them choose. If only 1, continue without asking.
            2. Ask how many days they want to buy a channel for, or how many items they want to spend.
         */

        return Mono.empty();
    }
}

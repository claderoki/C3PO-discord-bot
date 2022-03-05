package com.c3po.processors;

import com.c3po.command.milkyway.AvailablePurchase;
import com.c3po.command.milkyway.MilkywayItem;
import com.c3po.connection.repository.GuildRewardsRepository;
import com.c3po.connection.repository.ItemRepository;
import com.c3po.errors.PublicException;
import com.c3po.helper.setting.SettingScopeTarget;
import com.c3po.model.MilkywaySettings;
import com.c3po.model.PurchaseType;
import com.c3po.service.GuildRewardService;
import com.c3po.service.HumanService;
import com.c3po.service.MilkywayService;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

public class MilkywayProcessor {
    private MilkywaySettings settings;
    private final ChatInputInteractionEvent event;
    private boolean godmode;
    private SettingScopeTarget memberTarget;

    public MilkywayProcessor(ChatInputInteractionEvent event, boolean godmode) {
        this.event = event;
        this.godmode = godmode;
    }

    protected void validate() throws PublicException {
        // validate if godmode is OK.

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

    protected List<AvailablePurchase> getAvailablePurchases() {
        if (godmode) {
            return List.of(
                AvailablePurchase.builder()
                    .amount(999)
                    .daysWorth(999)
                    .purchaseType(PurchaseType.NONE)
                    .build()
            );
        }

        List<AvailablePurchase> availablePurchases = new ArrayList<>();

        List<MilkywayItem> items = MilkywayService.getItems();

        Integer humanId = HumanService.getHumanId(memberTarget.getUserId());
        Integer profileId = GuildRewardService.getProfileId(memberTarget);
        Integer points = GuildRewardsRepository.db().getPoints(profileId);

        int maxDays = points / settings.getCostPerDay();
        if (maxDays > 0) {
            availablePurchases.add(AvailablePurchase.builder()
                .amount(points)
                .daysWorth(maxDays)
                .label("Clovers")
                .purchaseType(PurchaseType.POINT)
                .build());
        }

        Map<Integer, Integer> itemAmounts = ItemRepository.db().getItemAmounts(humanId,
            items.stream().map(MilkywayItem::getItemId).toArray(Integer[]::new));

        for(MilkywayItem item: items) {
            Integer amount = itemAmounts.get(item.getItemId());
            if (amount > 0) {
                availablePurchases.add(AvailablePurchase.builder()
                    .amount(amount)
                    .label(item.getItemName())
                    .daysWorth(item.getDaysWorth() * amount)
                    .purchaseType(PurchaseType.ITEM)
                    .build());
            }
        }

        return availablePurchases;
    }

    protected ButtonInteractionEvent waitForButton(List<String> customIds) {
        Mono<Void> tempListener = event.getClient().on(ButtonInteractionEvent.class, buttonEvent -> {
                if (customIds.contains(buttonEvent.getCustomId())) {
                    return buttonEvent.reply("OK").withEphemeral(true);
                } else {
                    return Mono.empty();
                }})
            .timeout(Duration.ofMinutes(30))
            .onErrorResume(TimeoutException.class, ignore -> Mono.empty())
            .then();

        return null;
    }

    protected AvailablePurchase chooseAvailablePurchase(List<AvailablePurchase> availablePurchases) throws PublicException {
        if (availablePurchases.isEmpty()) {
            throw new PublicException("You have no means of paying.");
        } else if (availablePurchases.size() == 1) {
            return availablePurchases.get(0);
        } else {
            List<Button> buttons = new ArrayList<>();
            List<String> customIds = new ArrayList<>();
            for(AvailablePurchase availablePurchase: availablePurchases) {
                String customId = "purchase_" + availablePurchase.getLabel();
                buttons.add(Button.secondary(customId, availablePurchase.getLabel()));
                customIds.add(customId);
            }

            Void a = event.reply("Choose a payment type.").withComponents(ActionRow.of(buttons)).block();
            waitForButton(customIds);

            return availablePurchases.get(0);
        }
    }

    protected void load() throws PublicException {
        long guildId = event.getInteraction().getGuildId().orElseThrow().asLong();
        long userId = event.getInteraction().getUser().getId().asLong();

        settings = MilkywayService.getSettings(SettingScopeTarget.guild(guildId));
        memberTarget = SettingScopeTarget.member(userId, guildId);

        String a = "";
    }

    public void create() throws PublicException {
        load();
        validate();

        List<AvailablePurchase> availablePurchases = getAvailablePurchases();
        AvailablePurchase chosenPurchase = chooseAvailablePurchase(availablePurchases);
        String a = "";
    }

}

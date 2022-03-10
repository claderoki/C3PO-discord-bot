package com.c3po.processors;

import com.c3po.model.milkyway.AvailablePurchase;
import com.c3po.model.milkyway.MilkywayItem;
import com.c3po.connection.repository.GuildRewardsRepository;
import com.c3po.connection.repository.ItemRepository;
import com.c3po.connection.repository.MilkywayRepository;
import com.c3po.errors.PublicException;
import com.c3po.helper.EmbedHelper;
import com.c3po.helper.setting.SettingScopeTarget;
import com.c3po.helper.waiter.IntParser;
import com.c3po.helper.waiter.ParseResult;
import com.c3po.helper.waiter.Waiter;
import com.c3po.model.milkyway.Milkyway;
import com.c3po.model.milkyway.MilkywaySettings;
import com.c3po.model.milkyway.MilkywayStatus;
import com.c3po.model.milkyway.PurchaseType;
import com.c3po.service.GuildRewardService;
import com.c3po.service.HumanService;
import com.c3po.service.MilkywayService;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.reaction.ReactionEmoji;
import lombok.Getter;
import lombok.NonNull;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

@Getter
public class MilkywayProcessor {
    private final ChatInputInteractionEvent event;
    private final boolean godmode;

    private MilkywaySettings settings;
    private SettingScopeTarget memberTarget;
    private Integer humanId;
    private Integer profileId;

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

        Integer points = GuildRewardsRepository.db().getPoints(profileId);
        int maxDays = points / settings.getCostPerDay();
        if (maxDays > 0) {
            availablePurchases.add(AvailablePurchase.builder()
                .amount(points)
                .daysWorth(maxDays)
                .label("Clovers")
                .emoji("\uD83C\uDF40")
                .purchaseType(PurchaseType.POINT)
                .build());
        }

        List<MilkywayItem> items = MilkywayService.getItems();
        Map<Integer, Integer> itemAmounts = ItemRepository.db().getItemAmounts(humanId,
            items.stream().map(MilkywayItem::getItemId).toList());

        for(MilkywayItem item: items) {
            Integer amount = itemAmounts.get(item.getItemId());
            if (amount > 0) {
                availablePurchases.add(AvailablePurchase.builder()
                    .amount(amount)
                    .item(item)
                    .label(item.getItemName())
                    .daysWorth(item.getDaysWorth() * amount)
                    .emoji(item.getEmoji())
                    .purchaseType(PurchaseType.ITEM)
                    .build());
            }
        }

        return availablePurchases;
    }

    protected <T> Mono<T> waitFor(Map<String, T> options) {
        return event.getClient().on(ButtonInteractionEvent.class, buttonEvent -> {
                T purchase = options.get(buttonEvent.getCustomId());
                if (purchase != null) {
                    return Mono.just(purchase);
                }
                throw new RuntimeException("");
            })
            .timeout(Duration.ofSeconds(50))
            .onErrorResume(TimeoutException.class, ignore -> Mono.empty())
            .next();
    }

    @NonNull
    protected Mono<AvailablePurchase> chooseAvailablePurchase(List<AvailablePurchase> availablePurchases) throws PublicException {
        if (availablePurchases.isEmpty()) {
            throw new PublicException("You have no means of paying.");
        } else if (availablePurchases.size() == 1) {
            return Mono.just(availablePurchases.get(0));
        } else {
            List<Button> buttons = new ArrayList<>();
            Map<String, AvailablePurchase> options = new HashMap<>();
            for(AvailablePurchase availablePurchase: availablePurchases) {
                String customId = "purchase_" + availablePurchase.getLabel();
                buttons.add(Button.secondary(customId,
                    ReactionEmoji.unicode(availablePurchase.getEmoji()),
                    availablePurchase.getLabel())
                );
                options.put(customId, availablePurchase);
            }
            event.editReply()
                .withEmbeds(EmbedHelper.normal("Choose a payment type.").build())
                .withComponents(ActionRow.of(buttons)).block();
            return waitFor(options);
        }
    }

    protected void load() {
        long guildId = event.getInteraction().getGuildId().orElseThrow().asLong();
        long userId = event.getInteraction().getUser().getId().asLong();

        settings = MilkywayService.getSettings(SettingScopeTarget.guild(guildId));
        memberTarget = SettingScopeTarget.member(userId, guildId);

        if (!godmode) {
            // No point grabbing these since they won't be used if godmode is on anyway.
            humanId = HumanService.getHumanId(memberTarget.getUserId());
            profileId = GuildRewardService.getProfileId(memberTarget);
        }
    }

    protected Integer chooseDays(AvailablePurchase chosenPurchase) {
        if (chosenPurchase.getDaysWorth() == 1) {
            return 1;
        }

        Waiter waiter = new Waiter(event);
        waiter.setPrompt("You chose " + chosenPurchase.getLabel() + ", how many days worth would you like to spend?");

        IntParser parser = IntParser.builder().min(1).max(chosenPurchase.getDaysWorth()).build();
        parser.setEvent(event);

        ParseResult<Integer> result = waiter.wait(MessageCreateEvent.class, parser)
            .blockOptional()
            .orElseThrow();
        return result.getValueOrThrow();
    }

    private int getAmount(AvailablePurchase chosenPurchase, Integer daysChosen) {
        switch (chosenPurchase.getPurchaseType()) {
            case POINT -> {
                return daysChosen * settings.getCostPerDay();
            }
            case ITEM -> {
                return daysChosen / chosenPurchase.getItem().getDaysWorth();
            }
        }
        return chosenPurchase.getDaysWorth();
    }

    private void takePayment(AvailablePurchase chosenPurchase, Integer amount) {
        switch (chosenPurchase.getPurchaseType()) {
            case POINT -> GuildRewardsRepository.db().incrementPoints(profileId, -amount);
            case ITEM -> ItemRepository.db().spendItem(humanId, chosenPurchase.getItem().getItemId(), amount);
        }
    }

    public Milkyway create(String name, String description) throws PublicException {
        load();
        validate();

        List<AvailablePurchase> availablePurchases = getAvailablePurchases();
        AvailablePurchase chosenPurchase = chooseAvailablePurchase(availablePurchases).blockOptional().orElseThrow();
        Integer daysChosen = chooseDays(chosenPurchase);

        int amount = getAmount(chosenPurchase, daysChosen);
        Integer itemId = chosenPurchase.getPurchaseType().equals(PurchaseType.ITEM) ? chosenPurchase.getItem().getItemId() : null;

        Milkyway milkyway = Milkyway.builder()
            .amount(amount)
            .purchaseType(chosenPurchase.getPurchaseType())
            .daysPending(daysChosen)
            .identifier(MilkywayService.getIncrementIdentifier(memberTarget.getGuildId()))
            .name(name)
            .description(description != null ? description : name)
            .status(MilkywayStatus.PENDING)
            .itemId(itemId)
            .target(memberTarget)
            .build();

        MilkywayRepository.db().create(milkyway);
        takePayment(chosenPurchase, amount);

        return milkyway;
    }

}

package com.c3po.command.hangman.game.core;

import com.c3po.connection.repository.HumanRepository;
import com.c3po.core.command.Context;
import com.c3po.helper.Emoji;
import com.c3po.service.HumanService;
import com.c3po.ui.input.StartButtonMenuOption;
import com.c3po.ui.input.VoidMenuOption;
import com.c3po.ui.input.base.Menu;
import com.c3po.ui.input.base.MenuOption;
import discord4j.core.object.entity.User;
import lombok.Getter;
import lombok.Setter;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Setter
public class LobbyMenu extends Menu {
    @Getter
    private final Set<User> users = Collections.synchronizedSet(new HashSet<>());
    private int goldNeeded;

    public LobbyMenu(Context context, int goldNeeded) {
        super(context);
        this.goldNeeded = goldNeeded;
        ownerOnly = false;
        users.add(context.getEvent().getInteraction().getUser());
        refreshEmbed();

        for(MenuOption option: getMenuOptions()) {
            addOption(option);
        }
    }

    private List<MenuOption> getMenuOptions() {
        VoidMenuOption joinButton = new VoidMenuOption("Join");
        VoidMenuOption exitButton = new VoidMenuOption("Exit");
        joinButton.withEmoji("↩️");
        exitButton.withEmoji("\uD83D\uDEAA");
        users.add(context.getEvent().getInteraction().getUser());
        joinButton.setExecutor(c -> {
            int humanId = HumanService.getHumanId(c.getInteraction().getUser().getId());
            if (HumanRepository.db().getGold(humanId) < goldNeeded) {
                c.createFollowup()
                    .withContent(c.getInteraction().getUser().getMention() + ", you do not have enough gold.")
                    .subscribe(m -> m.delete().delaySubscription(Duration.ofSeconds(10)).subscribe()
                );
                return Mono.empty();
            }
            users.add(c.getInteraction().getUser());
            refreshEmbed();
            return Mono.empty();
        });
        exitButton.setExecutor(c -> {
            users.remove(c.getInteraction().getUser());
            refreshEmbed();
            return Mono.empty();
        });
        StartButtonMenuOption startButton = new StartButtonMenuOption("Start");
        startButton.withEmoji("▶️");
        startButton.setOwnerOnly(true);
        return List.of(joinButton, exitButton, startButton);
    }

    private void refreshEmbed() {
        String baseMessage = "[lobby]\n" + Emoji.EURO +" "+ goldNeeded + " to participate";
        String text = baseMessage + "\n" + users.stream().map(User::getMention).collect(Collectors.joining("\n"));
        embedConsumer = e -> e.description(text);
    }
}
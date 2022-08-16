package com.c3po.command.hangman.game.core;

import com.c3po.connection.repository.HumanRepository;
import com.c3po.core.command.Context;
import com.c3po.helper.Emoji;
import com.c3po.service.HumanService;
import com.c3po.ui.Toast;
import com.c3po.ui.ToastType;
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
    private final HumanRepository humanRepository = HumanRepository.db();
    private final HumanService humanService = new HumanService();

    @Getter
    private final Set<User> users = Collections.synchronizedSet(new HashSet<>());
    private int goldNeeded;

    public LobbyMenu(Context context, int goldNeeded) {
        super(context);
        this.goldNeeded = goldNeeded;
        ownerOnly = false;
        users.add(context.getEvent().getInteraction().getUser());
        refreshEmbed();

        for(MenuOption<?,?,?> option: getMenuOptions()) {
            addOption(option);
        }
    }

    public LobbyMenu(Context context) {
        this(context, 0);
    }

    private List<MenuOption<?,?,?>> getMenuOptions() {
        VoidMenuOption joinButton = new VoidMenuOption("Join");
        VoidMenuOption exitButton = new VoidMenuOption("Exit");
        joinButton.withEmoji("↩️");
        exitButton.withEmoji("\uD83D\uDEAA");
        users.add(context.getEvent().getInteraction().getUser());
        joinButton.setExecutor(c -> {
            if (goldNeeded > 0) {
                int humanId = humanService.getHumanId(c.getInteraction().getUser().getId());
                if (humanRepository.getGold(humanId) < goldNeeded) {
                    return context.sendToast(Toast.builder()
                        .message(c.getInteraction().getUser().getMention() + ", you do not have enough gold.")
                        .removeAfter(Duration.ofSeconds(10))
                        .type(ToastType.ERROR)
                        .build());
                }
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
        String baseMessage = "";
        if (goldNeeded > 0) {
            baseMessage = "[lobby]\n" + Emoji.EURO +" "+ goldNeeded + " to participate";
        }
        String text = baseMessage + "\n" + users.stream().map(User::getMention).collect(Collectors.joining("\n"));
        embedConsumer = e -> e.description(text);
    }
}

package com.c3po.command.guildrewards;

import com.c3po.command.Command;
import com.c3po.connection.repository.GuildRewardsRepository;
import com.c3po.model.GuildRewardsSettings;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.event.domain.interaction.DeferrableInteractionEvent;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.TimeoutException;


public class GuildRewardsSetupCommand extends Command {
    public String getName() {
        return "guildrewards setup";
    }

    public Mono<Void> handle(ChatInputInteractionEvent event) throws Exception {
        Long guildId = event.getInteraction().getGuildId().get().asLong();

        GuildRewardsSettings settings = GuildRewardsRepository.db().getSettings(guildId);
        if (settings == null) {
            settings = GuildRewardsSettings.builder()
                    .guildId(guildId)
                    .build();
        }

        DeferrableInteractionEvent reply = event.reply()
                .withComponents(ActionRow.of(Button.primary("custom-id", "Click me!!")))
                .withEphemeral(true)
                .withContent("Hello")
                .event()
        ;

        Mono<Void> tempListener = event.getClient().on(ButtonInteractionEvent.class, buttonEvent -> {
                    if (buttonEvent.getCustomId().equals("custom-id")) {
                        return buttonEvent.reply("You clicked me!").withEphemeral(true);
                    } else {
                        return Mono.empty();
                    }
                }).timeout(Duration.ofMinutes(30))
                .onErrorResume(TimeoutException.class, ignore -> Mono.empty())
                .then();

        return null;
    }
}

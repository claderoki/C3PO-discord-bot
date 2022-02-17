package com.c3po.command.guildrewards;

import com.c3po.command.Command;
import com.c3po.command.guildrewards.settings.BaseSetting;
import com.c3po.command.guildrewards.settings.MaxPointsPerMessage;
import com.c3po.connection.repository.GuildRewardsRepository;
import com.c3po.model.GuildRewardsSettings;
import com.c3po.ui.IntWaiter;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;


public class GuildRewardsSetupCommand extends Command {
    public String getName() {
        return "guildrewards setup";
    }

    public Mono<Void> handle(ChatInputInteractionEvent event) throws Exception {
        if (event.getInteraction().getGuildId().isEmpty()) {
            return Mono.empty();
        }
        long guildId = event.getInteraction().getGuildId().get().asLong();

        GuildRewardsSettings settings = GuildRewardsRepository.db().getSettings(guildId);
        if (settings == null) {
            settings = GuildRewardsSettings.builder()
                        .guildId(guildId)
                        .build();
        }

        ArrayList<BaseSetting<?>> configs = new ArrayList<>();
        configs.add(new MaxPointsPerMessage(settings));

        for (BaseSetting<?> config: configs) {
            config.input(event).block();
        }

        return event.createFollowup()
                .withContent("OK then, max is now " + settings.getMaxPointsPerMessage() + " and min is now " + settings.getMinPointsPerMessage())
                .then();
    }
}

//
//        ArrayList<BaseButton> buttons = new ArrayList<>() {{
//            add(new MaxPointPerMessageButton(settings));
//            add(new MinPointPerMessageButton(settings));
//            add(new TimeoutButton(settings));
//        }};
//
//        event.reply()
//                .withComponents(ActionRow.of(buttons.stream().map(BaseButton::getButton).toList()))
//                .withContent("What setting would you like to change?")
//                .block()
//        ;
//        ButtonWaiter buttonWaiter = new ButtonWaiter(event.getInteraction());
//        buttonWaiter.setButtons(buttons);
//        buttonWaiter.wait(event.getClient()).block();

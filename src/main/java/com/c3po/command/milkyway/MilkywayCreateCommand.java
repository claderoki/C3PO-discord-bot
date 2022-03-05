package com.c3po.command.milkyway;

import com.c3po.command.Command;
import com.c3po.connection.repository.ItemRepository;
import com.c3po.errors.PublicException;
import com.c3po.helper.setting.SettingScopeTarget;
import com.c3po.model.MilkywaySettings;
import com.c3po.model.PurchaseType;
import com.c3po.processors.MilkywayProcessor;
import com.c3po.service.GuildRewardService;
import com.c3po.service.HumanService;
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

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) throws Exception {
        MilkywayProcessor processor = new MilkywayProcessor(event, false);
        processor.create();

        /*
            1. Check what purchase options are available. and (optionally) let them choose. If only 1, continue without asking.
            2. Ask how many days they want to buy a channel for, or how many items they want to spend.
         */

        return Mono.empty();
    }
}

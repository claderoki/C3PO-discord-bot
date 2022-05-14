package com.c3po.ui.input;

import com.c3po.helper.waiter.EventParser;
import com.c3po.helper.waiter.ParseResult;
import com.c3po.helper.waiter.ResultType;
import com.c3po.helper.waiter.Waiter;
import com.c3po.ui.input.base.ButtonMenuOption;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import reactor.core.publisher.Mono;

public class WaiterMenuOption<F, E extends EventParser<F, MessageCreateEvent>> extends ButtonMenuOption<F> {
    private final E parser;

    public WaiterMenuOption(String name, E parser) {
        super(name);
        this.parser = parser;
    }

    public WaiterMenuOption(String name, E parser, F value) {
        super(name, value);
        this.parser = parser;
    }

    @Override
    public Mono<?> execute(ButtonInteractionEvent event) {
        parser.setEvent(context.getEvent());
        Waiter waiter = new Waiter(context.getEvent());
        waiter.setPrompt("Please enter a " + name);
        return event.deferEdit().then(waiter.wait(MessageCreateEvent.class, parser)).map(result -> {
            if (!result.getType().equals(ResultType.ERROR)) {
                setValue(result.getValueOrThrow());
            }
            return Mono.empty();
        });
    }
}

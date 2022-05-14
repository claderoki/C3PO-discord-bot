package com.c3po.ui.input;

import com.c3po.helper.waiter.IntParser;
import com.c3po.helper.waiter.ParseResult;
import com.c3po.helper.waiter.Waiter;
import com.c3po.ui.input.base.ButtonMenuOption;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import reactor.core.publisher.Mono;

public class LongMenuOption extends ButtonMenuOption<Long> {

    public LongMenuOption(String name) {
        super(name);
    }

    public LongMenuOption(String name, Long value) {
        super(name, value);
    }

    @Override
    public Mono<?> execute(ButtonInteractionEvent event) {
        IntParser parser = IntParser.builder().min(1).max(9999).build();
        parser.setEvent(context.getEvent());
        Waiter waiter = new Waiter(context.getEvent());
        waiter.setPrompt("Please enter that shit.");
        return event.deferEdit()
            .then(waiter.wait(MessageCreateEvent.class, parser)
            .map(ParseResult::getValueOrThrow)
            .map(value -> {
                setValue(Long.valueOf(value));
                return Mono.empty();
        }));
    }

}

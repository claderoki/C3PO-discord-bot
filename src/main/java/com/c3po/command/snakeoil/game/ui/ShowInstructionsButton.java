package com.c3po.command.snakeoil.game.ui;

import com.c3po.helper.Emoji;
import com.c3po.ui.input.base.ButtonMenuOption;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.interaction.ComponentInteractionEvent;
import reactor.core.publisher.Mono;

public class ShowInstructionsButton extends ButtonMenuOption<Void> {
    public ShowInstructionsButton() {
        super("Instructions");
        withEmoji(Emoji.WHITE_QUESTION_MARK);
    }

    @Override
    protected boolean isAllowed(ComponentInteractionEvent event) {
        return true;
    }

    @Override
    protected boolean isBottomRow() {
        return true;
    }

    @Override
    public Mono<Void> execute(ButtonInteractionEvent event) {
        return event.reply()
            .withEphemeral(true)
            .withContent("Instructions go here...");
    }
}

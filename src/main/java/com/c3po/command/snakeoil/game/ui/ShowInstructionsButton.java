package com.c3po.command.snakeoil.game.ui;

import com.c3po.helper.Emoji;
import com.c3po.ui.input.base.ButtonMenuOption;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import reactor.core.publisher.Mono;

public class ShowInstructionsButton extends ButtonMenuOption<Void> {
    public ShowInstructionsButton() {
        super("Instructions");
        setEmoji(Emoji.WHITE_QUESTION_MARK);
    }

    @Override
    public boolean isAllowed(ButtonInteractionEvent event) {
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

package com.c3po.command.pigeon;

import com.c3po.core.command.Context;
import com.c3po.helper.EmbedHelper;
import com.c3po.helper.PossibleParser;
import com.c3po.model.pigeon.Pigeon;
import com.c3po.model.pigeon.PigeonStatus;
import com.c3po.service.PigeonService;
import discord4j.core.spec.EmbedCreateFields;
import discord4j.core.spec.EmbedCreateSpec;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

public class PigeonProfileCommand extends PigeonSubCommand {
    protected PigeonProfileCommand(PigeonCommandGroup group) {
        super(group, "profile", "no description.");
    }

    private String getThumbnail(PigeonStatus status) {
        return switch (status) {
            case IDLE -> "https://media.discordapp.net/attachments/744172199770062899/863422058154033162/idle.png";
            case EXPLORING -> "https://media.discordapp.net/attachments/744172199770062899/863422074927317052/exploring.png";
            case JAILED -> "https://cdn.discordapp.com/attachments/744172199770062899/868835590211264542/jailed_pigeon.png";
            case SPACE_EXPLORING -> "https://media.discordapp.net/attachments/744172199770062899/863421831532511242/space_exploring.png";
            case MAILING, DATING, FIGHTING -> null;
        };
    }

    private String getFriendlyVerb(PigeonStatus status) {
        return switch (status) {
            case IDLE -> "idle";
            case MAILING -> "sending a mail";
            case EXPLORING -> "exploring";
            case FIGHTING -> "in a fight";
            case SPACE_EXPLORING -> "exploring space";
            case DATING -> "on a date";
            case JAILED -> "in jail";
        };
    }

    protected PigeonValidation getValidation() {
        return PigeonValidation.builder()
            .needsActivePigeon(true)
            .build();
    }

    @Override
    public Mono<?> execute(Context context) throws RuntimeException {
        long userId = context.getEvent().getInteraction().getUser().getId().asLong();

        PigeonValidation validation = getValidation();
        PigeonValidationResult result = validation.validate(userId);
        Pigeon pigeon = PigeonService.getPigeon(result.getPigeonId());

        EmbedCreateSpec embed = EmbedHelper.normal()
            .title(pigeon.getName())
            .description(pigeon.getStats().values().stream()
                .map((c) -> "%s %s".formatted(c.getEmoji(), c.getValue()))
                .collect(Collectors.joining("\n")))
            .footer(EmbedCreateFields.Footer.of(getFriendlyVerb(pigeon.getStatus()), null))
            .thumbnail(PossibleParser.toPossible(getThumbnail(pigeon.getStatus())))
            .build()
        ;

        return context.getEvent().reply().withEmbeds(embed);
    }
}

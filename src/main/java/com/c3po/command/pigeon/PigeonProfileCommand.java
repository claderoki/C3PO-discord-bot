package com.c3po.command.pigeon;

import com.c3po.command.pigeon.validation.PigeonValidationResult;
import com.c3po.command.pigeon.validation.PigeonValidationSettings;
import com.c3po.core.command.Context;
import com.c3po.helper.DiscordCommandOptionType;
import com.c3po.helper.EmbedHelper;
import com.c3po.helper.PossibleParser;
import com.c3po.model.pigeon.Pigeon;
import com.c3po.model.pigeon.PigeonStatus;
import discord4j.common.util.Snowflake;
import discord4j.core.spec.EmbedCreateFields;
import discord4j.core.spec.EmbedCreateSpec;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Component
public class PigeonProfileCommand extends PigeonSubCommand {

    protected PigeonProfileCommand() {
        super("profile", "no description.");
        this.addOption(option -> option.name("user")
            .description("The user whos pigeon you want to look at")
            .required(false)
            .type(DiscordCommandOptionType.USER.getValue()));
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

    protected PigeonValidationSettings getValidationSettings() {
        return PigeonValidationSettings.builder()
            .needsActivePigeon(true)
            .build();
    }

    private List<String> getAdditionalInfo(Pigeon pigeon) {
        return List.of("\uD83D\uDCAA " + pigeon.getGoldModifier());
    }

    private List<String> getBaseInfo(Pigeon pigeon) {
        return pigeon.getStats().values().stream().map(c -> "%s %s".formatted(c.getEmoji(), c.getValue())).toList();
    }

    private List<String> getInfo(Pigeon pigeon) {
        List<String> info = new ArrayList<>(getBaseInfo(pigeon));
        info.addAll(getAdditionalInfo(pigeon));
        return info;
    }

    @Override
    public Mono<Void> execute(Context context) throws RuntimeException {
        Snowflake userId = context.getOptions().optSnowflake("user");
        if (userId == null) {
            userId = context.getEvent().getInteraction().getUser().getId();
        }

        PigeonValidationSettings settings = getValidationSettings();
        PigeonValidationResult result = validation.validate(settings, userId.asLong());
        Pigeon pigeon = pigeonService.getPigeon(result.getPigeonId());

        EmbedCreateSpec embed = EmbedHelper.normal()
            .title(pigeon.getName())
            .description(String.join("\n", getInfo(pigeon)))
            .footer(EmbedCreateFields.Footer.of(getFriendlyVerb(pigeon.getStatus()), null))
            .thumbnail(PossibleParser.toPossible(getThumbnail(pigeon.getStatus())))
            .build()
        ;

        return context.getEvent().reply().withEmbeds(embed);
    }
}

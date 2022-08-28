package com.c3po.command.pigeon;

import com.c3po.command.pigeon.validation.PigeonValidationSettings;
import com.c3po.core.command.Context;
import com.c3po.helper.DiscordCommandOptionType;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
@Component
public class PigeonBuyCommand extends PigeonSubCommand {

    protected PigeonBuyCommand() {
        super("buy", "no description.");
        this.addOption(option -> option.name("name")
            .description("The name")
            .required(true)
            .type(DiscordCommandOptionType.STRING.getValue()));
    }

    protected PigeonValidationSettings getValidationSettings() {
        return PigeonValidationSettings.builder()
            .needsActivePigeon(false)
            .goldNeeded(250)
            .build();
    }

    @Override
    public Mono<Void> execute(Context context) throws RuntimeException {
        var settings = getValidationSettings();
        var result = validation.validate(settings, context.getEvent().getInteraction().getUser().getId().asLong());

        pigeonRepository.createPigeon(result.getHumanId(), context.getOptions().getString("name"));
        return context.getEvent().reply().withContent("PIGEON CREATED");
    }
}

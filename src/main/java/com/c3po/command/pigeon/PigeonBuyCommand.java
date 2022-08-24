package com.c3po.command.pigeon;

import com.c3po.command.pigeon.validation.PigeonValidation;
import com.c3po.core.command.Context;
import com.c3po.helper.DiscordCommandOptionType;
import reactor.core.publisher.Mono;

public class PigeonBuyCommand extends PigeonSubCommand {

    protected PigeonBuyCommand(PigeonCommandGroup group) {
        super(group, "buy", "no description.");
        this.addOption(option -> option.name("name")
            .description("The name")
            .required(true)
            .type(DiscordCommandOptionType.STRING.getValue()));
    }

    protected PigeonValidation getValidation() {
        return PigeonValidation.builder()
            .needsActivePigeon(false)
            .goldNeeded(250)
            .build();
    }

    @Override
    public Mono<Void> execute(Context context) throws RuntimeException {
        var validation = getValidation();
        var result = validation.validate(context.getEvent().getInteraction().getUser());

        pigeonRepository.createPigeon(result.getHumanId(), context.getOptions().getString("name"));
        return context.getEvent().reply().withContent("PIGEON CREATED");
    }
}

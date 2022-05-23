package com.c3po.command.pigeon;

import com.c3po.command.pigeon.validation.PigeonValidation;
import com.c3po.core.command.Context;
import com.c3po.errors.PublicException;
import com.c3po.helper.DiscordCommandOptionType;
import com.c3po.helper.EmbedHelper;
import com.c3po.helper.cache.flags.FlagController;
import com.c3po.helper.cache.flags.pigeon.PigeonLastPoop;
import com.c3po.helper.cache.flags.pigeon.PigeonLastPoopedOn;
import com.c3po.model.pigeon.PigeonStatus;
import com.c3po.model.pigeon.IdlePigeon;
import com.c3po.model.pigeon.PigeonWinnings;
import com.c3po.model.pigeon.stat.HumanGold;
import com.c3po.model.pigeon.stat.PigeonCleanliness;
import discord4j.common.util.Snowflake;
import discord4j.core.spec.EmbedCreateFields;
import reactor.core.publisher.Mono;

public class PigeonPoopCommand extends PigeonSubCommand {

    protected PigeonPoopCommand(PigeonCommandGroup group) {
        super(group, "poop", "no description.");
        this.addOption(option -> option.name("user")
            .description("The user you wish to order your pigeon to defecate on.")
            .required(false)
            .type(DiscordCommandOptionType.USER.getValue()));
    }

    private IdlePigeon getRandomIdlePigeon(Snowflake guildId, Snowflake authorId) {
        for(IdlePigeon pigeon: pigeonRepository.getIdlePigeons(guildId.asLong())) {
            if (pigeon.userId() == authorId.asLong()) {
                continue;
            }
            FlagController flagController = new FlagController(new PigeonLastPoopedOn(pigeon.id()));
            if (flagController.validate()) {
                return pigeon;
            }
        }
        return null;
    }

    private IdlePigeon getIdlePigeon(Context context, Snowflake userId) {
        if (userId == null) {
            IdlePigeon idlePigeon = getRandomIdlePigeon(context.getEvent().getInteraction().getGuildId().get(),
                context.getEvent().getInteraction().getUser().getId()
            );
            if (idlePigeon == null) {
                throw new PublicException("No pigeons available to defecate on.");
            }
            return idlePigeon;
        } else {
            var otherValidation = PigeonValidation.builder()
                .needsActivePigeon(true)
                .requiredPigeonStatus(PigeonStatus.IDLE)
                .build();
            var otherResult = otherValidation.validate(userId);
            return new IdlePigeon(otherResult.getPigeonId(), userId.asLong());
        }
    }

    @Override
    public Mono<?> execute(Context context) throws RuntimeException {
        Snowflake userId = context.getOptions().optSnowflake("user");
        var validation = PigeonValidation.builder()
            .needsActivePigeon(true)
            .requiredPigeonStatus(PigeonStatus.IDLE)
            .goldNeeded(userId == null ? 100 : 0)
            .build();

        var result = validation.validate(context.getEvent().getInteraction().getUser());

        FlagController poopController = new FlagController(new PigeonLastPoop(result.getPigeonId()));
        if (!poopController.validate()) {
            throw new PublicException("Your pigeon is not ready to defecate yet.");
        }

        IdlePigeon idlePigeon = getIdlePigeon(context, userId);

        PigeonWinnings initiatorWinnings = new PigeonWinnings();
        if (validation.getGoldNeeded() > 0) {
            initiatorWinnings.addStat(new HumanGold(-validation.getGoldNeeded()));
        }
        initiatorWinnings.addStat(new PigeonCleanliness(5));

        PigeonWinnings victimWinnings = new PigeonWinnings();
        victimWinnings.addStat(new PigeonCleanliness(-10));

        pigeonRepository.updateWinnings(result.getPigeonId(), initiatorWinnings);
        pigeonRepository.updateWinnings(idlePigeon.id(), victimWinnings);

        pigeonRepository.increasePoopCount(result.getPigeonId());
        pigeonRepository.increasePoopedOnCount(idlePigeon.id());

        String text = "%ss pigeon defecated on <@%s> pigeon. ".formatted(context.getEvent().getInteraction().getUser().getMention(), idlePigeon.userId());
        poopController.spend();
        new FlagController(new PigeonLastPoopedOn(idlePigeon.id())).spend();
        return context.getEvent().reply().withEmbeds(EmbedHelper.normal(text)
            .addField(EmbedCreateFields.Field.of(context.getEvent().getInteraction().getUser().getUsername(), initiatorWinnings.format(), false))
            .addField(EmbedCreateFields.Field.of("VICTIM", victimWinnings.format(), false))
            .build()
        );
    }
}

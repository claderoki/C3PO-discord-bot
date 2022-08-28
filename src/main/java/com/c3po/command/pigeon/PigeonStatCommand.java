package com.c3po.command.pigeon;

import com.c3po.command.pigeon.validation.PigeonValidationResult;
import com.c3po.command.pigeon.validation.PigeonValidationSettings;
import com.c3po.core.command.Context;
import com.c3po.error.PublicException;
import com.c3po.helper.EmbedHelper;
import com.c3po.model.pigeon.Pigeon;
import com.c3po.model.pigeon.PigeonStatus;
import com.c3po.model.pigeon.PigeonWinnings;
import com.c3po.model.pigeon.stat.*;
import com.c3po.model.pigeon.stat.core.Stat;
import reactor.core.publisher.Mono;

public abstract class PigeonStatCommand extends PigeonSubCommand {
    protected PigeonStatCommand(String name, String description) {
        super(name, description);
    }

    protected PigeonValidationSettings getValidationSettings() {
        return PigeonValidationSettings.builder()
            .needsActivePigeon(true)
            .requiredPigeonStatus(PigeonStatus.IDLE)
            .build();
    }

    protected abstract StatType getStatType();

    protected abstract int getGain();

    protected abstract int getCost();

    protected abstract String getMessage();

    @Override
    public Mono<Void> execute(Context context) throws RuntimeException {
        long userId = context.getEvent().getInteraction().getUser().getId().asLong();

        PigeonValidationSettings settings = getValidationSettings();
        PigeonValidationResult result = validation.validate(settings, userId);

        Pigeon pigeon = pigeonRepository.getPigeon(result.getPigeonId());
        StatType statType = getStatType();
        Stat stat = pigeon.getStat(statType);
        if (stat.getValue() == 100) {
            throw new PublicException("Your pigeon is already at max " + statType.toString().toLowerCase() + "!");
        }
        PigeonWinnings winnings = new PigeonWinnings();
        winnings.addStat(StatFactory.create(statType, getGain()));
        winnings.addStat(StatFactory.create(StatType.GOLD, -getCost()));
        pigeonRepository.updateWinnings(result.getPigeonId(), winnings);
        return context.getEvent().reply().withEmbeds(EmbedHelper.normal(getMessage() + "\n\n" + winnings.format()).build());
    }
}

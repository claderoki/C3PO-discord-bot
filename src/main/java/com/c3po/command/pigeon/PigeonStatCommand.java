package com.c3po.command.pigeon;

import com.c3po.connection.repository.PigeonRepository;
import com.c3po.core.command.Context;
import com.c3po.errors.PublicException;
import com.c3po.helper.EmbedHelper;
import com.c3po.model.pigeon.Pigeon;
import com.c3po.model.pigeon.PigeonStatus;
import com.c3po.model.pigeon.PigeonWinnings;
import com.c3po.model.pigeon.stat.*;
import com.c3po.model.pigeon.stat.core.Stat;
import com.c3po.service.PigeonService;
import reactor.core.publisher.Mono;

public abstract class PigeonStatCommand extends PigeonSubCommand {
    protected PigeonStatCommand(PigeonCommandGroup group, String name, String description) {
        super(group, name, description);
    }

    protected PigeonValidation getValidation() {
        return PigeonValidation.builder()
            .needsActivePigeon(true)
            .requiredPigeonStatus(PigeonStatus.IDLE)
            .build();
    }

    protected abstract StatType getStatType();

    protected abstract int getGain();

    protected abstract int getCost();

    @Override
    public Mono<?> execute(Context context) throws RuntimeException {
        long userId = context.getEvent().getInteraction().getUser().getId().asLong();

        PigeonValidation validation = getValidation();
        PigeonValidationResult result = validation.validate(userId);

        Pigeon pigeon = PigeonService.getPigeon(result.getPigeonId());
        StatType statType = getStatType();
        Stat stat = pigeon.getStat(statType);
        if (stat.getValue() == 100) {
            throw new PublicException("You are already max " + statType.toString().toLowerCase() + "!");
        }
        PigeonWinnings winnings = new PigeonWinnings();
        winnings.addStat(StatFactory.create(statType, getGain()));
        winnings.addStat(StatFactory.create(StatType.GOLD, -getCost()));
        PigeonRepository.db().updateWinnings(result.getPigeonId(), winnings);
        return context.getEvent().reply().withEmbeds(EmbedHelper.normal("You gain some and lose some\n\n" + winnings.format()).build());
    }
}

package com.c3po.command.pigeon;

import com.c3po.command.pigeon.validation.PigeonValidation;
import com.c3po.connection.repository.HumanRepository;
import com.c3po.core.command.Context;
import com.c3po.errors.PublicException;
import com.c3po.helper.EmbedHelper;
import com.c3po.helper.cache.flags.FlagController;
import com.c3po.helper.cache.flags.PigeonLastTrained;
import com.c3po.model.pigeon.Pigeon;
import com.c3po.model.pigeon.PigeonStatus;
import com.c3po.model.pigeon.PigeonWinnings;
import com.c3po.model.pigeon.stat.HumanGold;
import reactor.core.publisher.Mono;

public class PigeonTrainCommand extends PigeonSubCommand {
    private final HumanRepository humanRepository = HumanRepository.db();

    protected PigeonTrainCommand(PigeonCommandGroup group) {
        super(group, "train", "no description.");
    }

    protected PigeonValidation getValidation() {
        return PigeonValidation.builder()
            .needsActivePigeon(true)
            .requiredPigeonStatus(PigeonStatus.IDLE)
            .build();
    }

    @Override
    public Mono<?> execute(Context context) throws RuntimeException {
        var validation = getValidation();
        var result = validation.validate(context.getEvent().getInteraction().getUser());
        FlagController flagController = new FlagController(new PigeonLastTrained(result.getPigeonId()));
        if (!flagController.validate()) {
            throw new PublicException("Your pigeon is still on a break from its previous exercise session.");
        }

        Pigeon pigeon = pigeonService.getPigeon(result.getPigeonId());

        double increase = 0.01;
        double newGoldModifier = pigeon.getGoldModifier() + increase;

        if (newGoldModifier > 1.5) {
            throw new PublicException("Can't go over 1.5 for now.");
        }

        PigeonWinnings pigeonWinnings = new PigeonWinnings();
        pigeonWinnings.setGoldModifier(increase);

        var baseCost = 100.0;
        var cost = baseCost * (pigeon.getGoldModifier() * 3.0);

        var gold = humanRepository.getGold(result.getHumanId());
        if (gold < cost) {
            throw new PublicException("You do not have enough gold to train your pigeon. %s needed".formatted(gold));
        }

        pigeonWinnings.addStat(new HumanGold(-(int)cost));
        pigeonRepository.updateWinnings(pigeon.getId(), pigeonWinnings);

        return context.getEvent().reply().withEmbeds(EmbedHelper.normal("Your pigeon bulks up\n" + pigeonWinnings.format()).build())
            .then(Mono.defer(() -> {
                flagController.spend();
                return Mono.empty();
        }));
    }
}

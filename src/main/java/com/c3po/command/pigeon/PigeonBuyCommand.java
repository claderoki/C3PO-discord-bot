package com.c3po.command.pigeon;

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

public class PigeonBuyCommand extends PigeonSubCommand {

    protected PigeonBuyCommand(PigeonCommandGroup group) {
        super(group, "buy", "no description.");
    }

    protected PigeonValidation getValidation() {
        return PigeonValidation.builder()
            .needsActivePigeon(false)
            .build();
    }

    @Override
    public Mono<?> execute(Context context) throws RuntimeException {
        var validation = getValidation();
        var result = validation.validate(context.getEvent().getInteraction().getUser());
        return Mono.empty();
    }
}

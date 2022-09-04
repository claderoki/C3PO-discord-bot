package com.c3po.command.insecta;

import com.c3po.command.insecta.core.*;
import com.c3po.command.insecta.model.InsectaWinningDTO;
import com.c3po.core.command.Context;
import com.c3po.helper.DateTimeHelper;
import com.c3po.helper.DiscordCommandOptionType;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class InsectaBuyCommand extends InsectaSubCommand {
    protected InsectaBuyCommand() {
        super("buy");
        addOption(o -> o
            .name("type")
            .description("no description")
            .type(DiscordCommandOptionType.STRING.getValue())
            .required(true));

        addOption(o -> o
            .name("amount")
            .description("no description")
            .type(DiscordCommandOptionType.INTEGER.getValue())
            .required(false));
    }

    @Override
    protected Mono<Void> execute(Context context) {
        String type = context.getOptions().getString("type");
        Long amount = context.getOptions().optLong("amount");
        if (amount == null) {
            amount = 1L;
        }
        Insecta insecta = InsectaFactory.get(type);
        if (insecta == null) {
            return Mono.error(new Exception("Not valid."));
        }
        long userId = context.getEvent().getInteraction().getUser().getId().asLong();
        InsectaProfile profile = insectaRepository.getProfile(userId);
        if (profile == null) {
            profile = InsectaProfile.builder()
                .id(null)
                .hexacoin(50)
                .insectarium(new Insectarium())
                .lastCollected(DateTimeHelper.now())
                .userId(userId)
                .build();
            insectaRepository.createProfile(profile);
        }
        long cost = (insecta.getCost()*amount);
        if (profile.getHexacoin() < cost) {
            return Mono.error(new Exception("You can't afford this."));
        }

        InsectaWinnings winnings = insectaService.collect(profile);
        winnings.getValues().forEach((k, v) -> insectaRepository.saveWinnings(new InsectaWinningDTO(k.getKey(), userId, v)));

        profile.getInsectarium().add(insecta, amount);
        profile.setHexacoin(profile.getHexacoin() - cost);
        profile.setLastCollected(DateTimeHelper.now());
        insectaRepository.updateProfile(profile);
        return context.getReplier().reply().withContent("OK.");
    }
}

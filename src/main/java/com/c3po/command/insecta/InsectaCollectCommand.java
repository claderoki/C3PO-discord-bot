package com.c3po.command.insecta;

import com.c3po.command.insecta.core.InsectaFactory;
import com.c3po.command.insecta.core.InsectaProfile;
import com.c3po.command.insecta.core.InsectaWinnings;
import com.c3po.command.insecta.model.InsectaWinningDTO;
import com.c3po.core.command.Context;
import com.c3po.helper.DateTimeHelper;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class InsectaCollectCommand extends InsectaSubCommand {
    protected InsectaCollectCommand() {
        super("collect");
    }

    @Override
    protected Mono<Void> execute(Context context) throws RuntimeException {
        long userId = context.getEvent().getInteraction().getUser().getId().asLong();
        InsectaProfile profile = insectaRepository.getProfile(userId);
        if (profile == null) {
            return Mono.error(new Exception("No profile...."));
        }
        List<InsectaWinningDTO> uncollectedWinnings =  insectaRepository.getUncollectedWinnings(userId);
        InsectaWinnings winnings = insectaService.collect(profile);
        winnings.getValues().forEach((k, v) -> insectaRepository.saveWinnings(new InsectaWinningDTO(k.getKey(), userId, v)));
        uncollectedWinnings.forEach(w -> winnings.add(InsectaFactory.get(w.getKey()), w.getValue()));
        long totalGained = winnings.getValues().values().stream().mapToLong(c -> c).sum();
        profile.setHexacoin(profile.getHexacoin() + totalGained);
        profile.setLastCollected(DateTimeHelper.now());
        insectaRepository.updateProfile(profile);
        insectaRepository.setCollectedAll(userId);
        var values = winnings.getValues().entrySet().stream().map(e -> e.getKey().getKey() +" => +" +  e.getValue()).collect(Collectors.joining("\n"));
        return context.getReplier().reply().withContent("You did it, you get: " + values + "\nTOTAL: " + totalGained);
    }
}

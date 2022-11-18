package com.c3po.command.insecta;

import com.c3po.command.insecta.core.InsectaFactory;
import com.c3po.command.insecta.core.InsectaProfile;
import com.c3po.command.insecta.core.InsectaWinnings;
import com.c3po.command.insecta.model.InsectaWinningDTO;
import com.c3po.command.insecta.ui.InsectaUI;
import com.c3po.core.command.Context;
import com.c3po.error.PublicException;
import com.c3po.helper.DateTimeHelper;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

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
            return Mono.error(new PublicException("No profile...."));
        }
        List<InsectaWinningDTO> uncollectedWinnings =  insectaRepository.getUncollectedWinnings(userId);

        LocalDateTime firstCollected = uncollectedWinnings.stream()
            .map(InsectaWinningDTO::getInitialDate)
            .min(Comparator.comparingLong(c -> c.toEpochSecond(DateTimeHelper.offset)))
            .orElse(profile.getLastCollected())
        ;

        InsectaWinnings winnings = insectaService.collect(profile);
        winnings.getValues().forEach((k, v) -> insectaRepository.saveWinnings(InsectaWinningDTO.builder()
                .initialDate(profile.getLastCollected())
                .collected(false)
                .key(k.getKey())
                .value(v)
                .userId(userId)
            .build()));

        uncollectedWinnings.forEach(w -> winnings.add(InsectaFactory.get(w.getKey()), w.getValue()));
        long totalGained = winnings.getValues().values().stream().mapToLong(c -> c).sum();
        profile.incrementHexacoin(totalGained);
        profile.setLastCollected(DateTimeHelper.now());
        insectaRepository.updateProfile(profile);
        insectaRepository.removeAllCollected(userId);
        InsectaUI ui = new InsectaUI(context.getInteractor());
        return ui.sendCollectOverview(winnings, profile, firstCollected);
    }
}
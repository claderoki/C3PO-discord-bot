package com.c3po.command.pigeon;

import com.c3po.command.pigeon.menu.scenario.ScenarioMenu;
import com.c3po.command.pigeon.validation.PigeonValidationResult;
import com.c3po.command.pigeon.validation.PigeonValidationSettings;
import com.c3po.connection.repository.ExplorationRepository;
import com.c3po.connection.repository.HumanRepository;
import com.c3po.connection.repository.StreakRepository;
import com.c3po.core.command.Context;
import com.c3po.helper.DateTimeDelta;
import com.c3po.helper.DateTimeHelper;
import com.c3po.helper.EmbedHelper;
import com.c3po.model.exploration.*;
import com.c3po.model.pigeon.Pigeon;
import com.c3po.model.pigeon.PigeonStatus;
import com.c3po.model.pigeon.PigeonWinnings;
import com.c3po.service.ExplorationService;
import com.c3po.ui.input.base.MenuManager;
import discord4j.core.spec.EmbedCreateFields;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Component
public class PigeonSpaceCommand extends PigeonSubCommand {
    @Autowired
    protected ExplorationService explorationService;

    @Autowired
    protected ExplorationRepository explorationRepository;

    @Autowired
    protected HumanRepository humanRepository;

    @Autowired
    protected StreakRepository streakRepository;

    @Autowired
    private AutowireCapableBeanFactory beanFactory;

    protected PigeonSpaceCommand() {
        super("space", "no description.");
    }

    protected PigeonValidationSettings getValidationSettings() {
        return PigeonValidationSettings.builder()
            .needsActivePigeon(true)
            .requiredPigeonStatus(PigeonStatus.SPACE_EXPLORING)
            .build();
    }

    private List<ExplorationBonus> getBonuses(long humanId) {
        return List.of();
    }

    private Mono<Void> finalSequence(Context context, Pigeon pigeon, Exploration exploration, List<PigeonWinnings> totalWinnings) {
        PigeonWinnings winnings = PigeonWinnings.merge(totalWinnings.toArray(PigeonWinnings[]::new));

        LocalDateTime now = DateTimeHelper.now();
        long seconds = ChronoUnit.SECONDS.between(exploration.getStartDate(), now);
        DateTimeDelta delta = DateTimeDelta.fromSeconds(seconds);

        var embed = EmbedHelper.normal("After %s of exploring %s, your pigeon finally returns home.".formatted(delta.format(), "Luna"))
            .addField(EmbedCreateFields.Field.of("Stats", winnings.format(), false))
        ;

        var bonuses = getBonuses(pigeon.getHumanId());
        for (ExplorationBonus bonus: bonuses) {
            embed.addField(bonus.text(), bonus.winnings().format(), false);
            winnings.add(bonus.winnings());
        }

        pigeonRepository.updateWinnings(pigeon.getId(), winnings);
        humanRepository.addItems(winnings.getItemIds(), pigeon.getHumanId());
        return context.getEvent().createFollowup().withEmbeds(embed.build()).then();
    }

    private Mono<Void> executeFinalSequence(Pigeon pigeon, Context context, Exploration exploration, List<ExplorationScenarioWinnings> totalWinnings) {
        if (totalWinnings.size() == exploration.getTotalActions()) {
            return finalSequence(context, pigeon, exploration, totalWinnings.stream().map(ExplorationScenarioWinnings::pigeonWinnings).toList());
        } else {
            List<PigeonWinnings> winnings = new ArrayList<>(pigeonRepository.getWinnings(exploration.getId()));
            winnings.addAll(totalWinnings.stream().map(ExplorationScenarioWinnings::pigeonWinnings).toList());
            return finalSequence(context, pigeon, exploration, winnings);
        }
    }

    private Mono<Void> executeScenarios(Pigeon pigeon, Context context, Exploration exploration) {
        FullExplorationLocation location = explorationService.getAllLocations().get(exploration.getLocationId());
        ScenarioMenu menu = new ScenarioMenu(beanFactory, context, location, exploration, pigeon);
        return new MenuManager<>(menu).waitFor().flatMap(c -> {
            List<ExplorationScenarioWinnings> totalWinnings = menu.getTotalWinnings();
            for (var winnings: totalWinnings) {
                explorationRepository.createWinnings(exploration.getId(), winnings.actionId(), winnings.pigeonWinnings());
            }

            int actionsRemaining = exploration.getActionsRemaining() - menu.getOptionsHandled();
            if (actionsRemaining <= 0) {
                return executeFinalSequence(pigeon, context, exploration, totalWinnings)
                    .then(Mono.fromRunnable(() -> pigeonRepository.updateStatus(pigeon.getId(), PigeonStatus.IDLE)))
                    .then(Mono.fromRunnable(() -> explorationRepository.finish(exploration.getId())));
            }

            explorationRepository.updateActionsRemaining(exploration.getId(), actionsRemaining);
            return Mono.empty();
        });
    }

    @Override
    public Mono<Void> execute(Context context) throws RuntimeException {
        long userId = context.getEvent().getInteraction().getUser().getId().asLong();


        PigeonValidationSettings settings = getValidationSettings();
        PigeonValidationResult result = validation.validate(settings, userId);

        Exploration exploration = explorationRepository.getExploration(result.getPigeonId());
        LocalDateTime now = DateTimeHelper.now();

        if (now.isAfter(exploration.getArrivalDate())) {
            Pigeon pigeon = pigeonService.getPigeon(result.getPigeonId());
            return executeScenarios(pigeon, context, exploration);
        } else {
            return context.getEvent().reply().withContent("not there yet");
        }
    }
}

package com.c3po.command.pigeon;

import com.c3po.connection.repository.ExplorationRepository;
import com.c3po.connection.repository.HumanRepository;
import com.c3po.connection.repository.PigeonRepository;
import com.c3po.core.command.Context;
import com.c3po.helper.DateTimeDelta;
import com.c3po.helper.DateTimeHelper;
import com.c3po.helper.EmbedHelper;
import com.c3po.helper.RandomHelper;
import com.c3po.model.exploration.*;
import com.c3po.model.pigeon.PigeonStatus;
import com.c3po.model.pigeon.PigeonWinnings;
import com.c3po.model.pigeon.stat.*;
import com.c3po.service.ExplorationService;
import com.c3po.ui.input.SingleUseButtonMenuOption;
import com.c3po.ui.input.base.Menu;
import com.c3po.ui.input.base.MenuManager;
import discord4j.core.spec.EmbedCreateFields;
import discord4j.core.spec.EmbedCreateSpec;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

public class PigeonSpaceCommand extends PigeonSubCommand {
    protected PigeonSpaceCommand(PigeonCommandGroup group) {
        super(group, "space", "no description.");
    }

    protected PigeonValidation getValidation() {
        return PigeonValidation.builder()
            .needsActivePigeon(true)
            .requiredPigeonStatus(PigeonStatus.SPACE_EXPLORING)
            .build();
    }

    private PigeonWinnings toWinnings(ExplorationScenario scenario) {
        PigeonWinnings winnings = new PigeonWinnings();
        List<Stat> stats = new ArrayList<>();
        stats.add(new HumanGold(scenario.getGold()));
        stats.add(new PigeonHealth(scenario.getHealth()));
        stats.add(new PigeonHappiness(scenario.getHappiness()));
        stats.add(new PigeonExperience(scenario.getExperience()));
        stats.add(new PigeonCleanliness(scenario.getCleanliness()));
        stats.add(new PigeonFood(scenario.getFood()));
        winnings.setStats(new LinkedHashMap<>(stats.stream().collect(Collectors.toMap(Stat::getStatType, (v) -> v))));

        Integer itemId = scenario.getItemId();
        if (scenario.getItemCategoryId() != null) {
            // replace itemid here with a random item from this category.
        }
        if (itemId != null) {
            winnings.setItemIds(List.of(itemId));
        }
        return winnings;
    }

    private List<ExplorationBonus> getBonuses() {
        return List.of();
    }

    private Mono<?> finalSequence(Context context, PigeonValidationResult result, Exploration exploration, List<PigeonWinnings> totalWinnings) {
        PigeonWinnings winnings = PigeonWinnings.merge(totalWinnings.toArray(PigeonWinnings[]::new));

        LocalDateTime now = DateTimeHelper.now();
        long seconds = ChronoUnit.SECONDS.between(exploration.getStartDate(), now);
        DateTimeDelta delta = DateTimeDelta.fromSeconds(seconds);

        var embed = EmbedHelper.normal("After %s of exploring %s, your pigeon finally returns home.".formatted(delta.format(), "Luna"))
            .addField(EmbedCreateFields.Field.of("Stats", winnings.format(), false));
        //TODO: add bonus fields here
        PigeonRepository.db().updateWinnings(result.getPigeonId(), winnings);
        HumanRepository.db().addItems(winnings.getItemIds(), result.getHumanId());
        return context.getEvent().createFollowup().withEmbeds(embed.build()).then();
    }

    private Mono<?> executeScenarios(PigeonValidationResult result, Context context, Exploration exploration) {
        FullExplorationLocation location = ExplorationService.getAllLocations().get(exploration.getLocationId());
        Menu menu = new Menu(context);
        menu.setMaximumOptionsAllowed(exploration.getActionsRemaining());
            List<ExplorationScenarioWinnings> totalWinnings = new ArrayList<>();

        for (var action: location.actions()) {
            SingleUseButtonMenuOption menuOption = new SingleUseButtonMenuOption(action.name());
            menuOption.withEmoji(action.symbol());
            menuOption.setExecutor(c -> {
                ExplorationScenario scenario = RandomHelper.choice(action.scenarios());
                PigeonWinnings winnings = toWinnings(scenario);
                totalWinnings.add(new ExplorationScenarioWinnings(winnings, action.id()));
                return c.createFollowup().withEmbeds(EmbedHelper.base()
                    .title(action.symbol() + " " + action.name())
                    .description(scenario.getText() + "\n\n" + winnings.format())
                    .build());
            });

            menu.addOption(menuOption);
        }

        String text = "You arrive at %s (%s).\n\nWhat action would you like to perform?\n";
        MenuManager.waitForMenu(menu, (e) -> e
            .description(text.formatted(location.planetName(), location.name()))
            .color(EmbedHelper.COLOR)
            .thumbnail(location.imageUrl()))
            .block();

        for (var winnings: totalWinnings) {
            ExplorationRepository.db().createWinnings(exploration.getId(), winnings.actionId(), winnings.pigeonWinnings());
        }

        int actionsRemaining = exploration.getActionsRemaining() - menu.getOptionsHandled();
        if (actionsRemaining == 0) {
            if (totalWinnings.size() == exploration.getTotalActions()) {
                finalSequence(context, result, exploration, totalWinnings.stream().map(ExplorationScenarioWinnings::pigeonWinnings).toList()).block();
            } else {
                List<PigeonWinnings> winnings = new ArrayList<>(PigeonRepository.db().getWinnings(exploration.getId()));
                winnings.addAll(totalWinnings.stream().map(ExplorationScenarioWinnings::pigeonWinnings).toList());
                finalSequence(context, result, exploration, winnings).block();
            }
            PigeonRepository.db().updateStatus(result.getPigeonId(), PigeonStatus.IDLE);
            ExplorationRepository.db().finish(exploration.getId());
        } else {
            ExplorationRepository.db().updateActionsRemaining(exploration.getId(), actionsRemaining);
        }

        return Mono.empty();
    }


    @Override
    public Mono<?> execute(Context context) throws RuntimeException {
        long userId = context.getEvent().getInteraction().getUser().getId().asLong();

        PigeonValidation validation = getValidation();
        PigeonValidationResult result = validation.validate(userId);

        Exploration exploration = ExplorationRepository.db().getExploration(result.getPigeonId());
        LocalDateTime now = DateTimeHelper.now();

        if (now.isAfter(exploration.getArrivalDate())) {
            return executeScenarios(result, context, exploration);
        } else {
            return context.getEvent().reply().withContent("not there yet");
        }
    }
}

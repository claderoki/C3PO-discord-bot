package com.c3po.command.pigeon;

import com.c3po.connection.repository.ExplorationRepository;
import com.c3po.connection.repository.HumanRepository;
import com.c3po.core.command.Context;
import com.c3po.helper.DateTimeDelta;
import com.c3po.helper.DateTimeHelper;
import com.c3po.helper.EmbedHelper;
import com.c3po.helper.RandomHelper;
import com.c3po.model.exploration.*;
import com.c3po.model.item.ItemCategory;
import com.c3po.model.pigeon.Pigeon;
import com.c3po.model.pigeon.PigeonStatus;
import com.c3po.model.pigeon.PigeonWinnings;
import com.c3po.model.pigeon.stat.*;
import com.c3po.model.pigeon.stat.core.Stat;
import com.c3po.service.ExplorationService;
import com.c3po.service.ItemService;
import com.c3po.ui.input.SingleUseButtonMenuOption;
import com.c3po.ui.input.base.Menu;
import com.c3po.ui.input.base.MenuManager;
import discord4j.core.spec.EmbedCreateFields;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class PigeonSpaceCommand extends PigeonSubCommand {
    protected final ExplorationService explorationService = new ExplorationService();
    protected final ItemService itemService = new ItemService();
    protected final ExplorationRepository explorationRepository;
    protected final HumanRepository humanRepository;

    protected PigeonSpaceCommand(PigeonCommandGroup group) {
        super(group, "space", "no description.");
        humanRepository = HumanRepository.db();
        explorationRepository = ExplorationRepository.db();
    }

    protected PigeonValidation getValidation() {
        return PigeonValidation.builder()
            .needsActivePigeon(true)
            .requiredPigeonStatus(PigeonStatus.SPACE_EXPLORING)
            .build();
    }

    private PigeonWinnings toWinnings(ExplorationScenario scenario) {
        PigeonWinnings winnings = new PigeonWinnings();
        winnings.setStats(List.of(
            new HumanGold(scenario.getGold()),
            new PigeonHealth(scenario.getHealth()),
            new PigeonHappiness(scenario.getHappiness()),
            new PigeonExperience(scenario.getExperience()),
            new PigeonCleanliness(scenario.getCleanliness()),
            new PigeonFood(scenario.getFood())
        ));

        Integer itemId = scenario.getItemId();
        if (scenario.getItemCategoryId() != null) {
            ItemCategory category = itemService.getAllCategories().get(scenario.getItemCategoryId());
            itemId = RandomHelper.choice(category.allItemIds());
        }
        if (itemId != null) {
            winnings.addItemId(itemId);
        }
        return winnings;
    }

    private List<ExplorationBonus> getBonuses() {
        return List.of();
    }

    private Mono<?> finalSequence(Context context, Pigeon pigeon, Exploration exploration, List<PigeonWinnings> totalWinnings) {
        PigeonWinnings winnings = PigeonWinnings.merge(totalWinnings.toArray(PigeonWinnings[]::new));

        LocalDateTime now = DateTimeHelper.now();
        long seconds = ChronoUnit.SECONDS.between(exploration.getStartDate(), now);
        DateTimeDelta delta = DateTimeDelta.fromSeconds(seconds);

        var embed = EmbedHelper.normal("After %s of exploring %s, your pigeon finally returns home.".formatted(delta.format(), "Luna"))
            .addField(EmbedCreateFields.Field.of("Stats", winnings.format(), false));
        //TODO: add bonus fields here
        pigeonRepository.updateWinnings(pigeon.getId(), winnings);
        humanRepository.addItems(winnings.getItemIds(), pigeon.getHumanId());
        return context.getEvent().createFollowup().withEmbeds(embed.build()).then();
    }

    private Menu createMenu(Pigeon pigeon, Context context, Exploration exploration, List<ExplorationScenarioWinnings> totalWinnings) {
        FullExplorationLocation location = explorationService.getAllLocations().get(exploration.getLocationId());
        Menu menu = new Menu(context);
        menu.setMaximumOptionsAllowed(exploration.getActionsRemaining());
        String text = "You arrive at %s (%s).\n\nWhat action would you like to perform?\n";
        menu.setEmbedConsumer(e -> e
            .description(text.formatted(location.planetName(), location.name()))
            .color(EmbedHelper.COLOR)
            .thumbnail(location.imageUrl()));
        for (var action: location.actions()) {
            SingleUseButtonMenuOption menuOption = new SingleUseButtonMenuOption(action.name());
            menuOption.withEmoji(action.symbol());
            menuOption.setExecutor(c -> {
                ExplorationScenario scenario = RandomHelper.choice(action.scenarios());
                PigeonWinnings winnings = toWinnings(scenario);
                Stat gold = winnings.getStat(StatType.GOLD);
                gold.setValue((long) (gold.getValue() * pigeon.getGoldModifier()));
                totalWinnings.add(new ExplorationScenarioWinnings(winnings, action.id()));
                return c.createFollowup().withEmbeds(EmbedHelper.base()
                    .title(action.symbol() + " " + action.name())
                    .description(scenario.getText() + "\n\n" + winnings.format())
                    .build());
            });

            menu.addOption(menuOption);
        }
        return menu;
    }

    private Mono<?> executeFinalSequence(Pigeon pigeon, Context context, Exploration exploration, List<ExplorationScenarioWinnings> totalWinnings) {
        if (totalWinnings.size() == exploration.getTotalActions()) {
            return finalSequence(context, pigeon, exploration, totalWinnings.stream().map(ExplorationScenarioWinnings::pigeonWinnings).toList());
        } else {
            List<PigeonWinnings> winnings = new ArrayList<>(pigeonRepository.getWinnings(exploration.getId()));
            winnings.addAll(totalWinnings.stream().map(ExplorationScenarioWinnings::pigeonWinnings).toList());
            return finalSequence(context, pigeon, exploration, winnings);
        }
    }

    private Mono<?> executeScenarios(Pigeon pigeon, Context context, Exploration exploration) {
        List<ExplorationScenarioWinnings> totalWinnings = new ArrayList<>();
        Menu menu = createMenu(pigeon, context, exploration, totalWinnings);
        return MenuManager.waitForMenu(menu).flatMap(c -> {
            for (var winnings: totalWinnings) {
                explorationRepository.createWinnings(exploration.getId(), winnings.actionId(), winnings.pigeonWinnings());
            }

            int actionsRemaining = exploration.getActionsRemaining() - menu.getOptionsHandled();
            if (actionsRemaining == 0) {
                return executeFinalSequence(pigeon, context, exploration, totalWinnings).then(Mono.defer(() -> {
                    pigeonRepository.updateStatus(pigeon.getId(), PigeonStatus.IDLE);
                    explorationRepository.finish(exploration.getId());
                    return Mono.empty();
                }));
            }

            explorationRepository.updateActionsRemaining(exploration.getId(), actionsRemaining);
            return Mono.empty();
        });
    }

    @Override
    public Mono<?> execute(Context context) throws RuntimeException {
        long userId = context.getEvent().getInteraction().getUser().getId().asLong();

        PigeonValidation validation = getValidation();
        PigeonValidationResult result = validation.validate(userId);

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

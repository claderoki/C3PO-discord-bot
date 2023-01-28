package com.c3po.command.pigeon.menu.scenario.option;

import com.c3po.helper.EmbedHelper;
import com.c3po.helper.RandomHelper;
import com.c3po.model.exploration.ExplorationAction;
import com.c3po.model.exploration.ExplorationScenario;
import com.c3po.model.exploration.ExplorationScenarioWinnings;
import com.c3po.model.item.ItemCategory;
import com.c3po.model.pigeon.PigeonWinnings;
import com.c3po.model.pigeon.stat.*;
import com.c3po.model.pigeon.stat.core.Stat;
import com.c3po.service.ItemService;
import com.c3po.ui.input.SingleUseButtonMenuOption;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import lombok.Getter;
import reactor.core.publisher.Mono;

import java.util.List;

public class ScenarioMenuOption extends SingleUseButtonMenuOption {
    protected final ItemService itemService;
    private final ExplorationAction action;
    private final double goldModifier;
    @Getter
    private ExplorationScenarioWinnings winnings;

    public ScenarioMenuOption(ItemService itemService, ExplorationAction action, double goldModifier) {
        super(action.name());
        this.itemService = itemService;
        emoji = action.symbol();
        this.action = action;
        this.goldModifier = goldModifier;
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

    @Override
    public Mono<Void> execute(ButtonInteractionEvent event) {
        return super.execute(event).then(Mono.defer(() -> {
            ExplorationScenario scenario = RandomHelper.choice(action.scenarios());
            PigeonWinnings winnings = toWinnings(scenario);
            Stat gold = winnings.getStat(StatType.GOLD);
            if (gold.getValue() > 0) {
                gold.setValue((long) (gold.getValue() * goldModifier));
            } else {
                gold.setValue(gold.getValue());
            }
            this.winnings = new ExplorationScenarioWinnings(winnings, action.id());
            return event.createFollowup().withEmbeds(EmbedHelper.base()
                .title(action.symbol() + " " + action.name())
                .description(scenario.getText() + "\n\n" + winnings.format())
                .build());
        })).then();
    }
}

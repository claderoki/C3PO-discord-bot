package com.c3po.command.pigeon.menu.scenario;

import com.c3po.command.pigeon.menu.scenario.option.ScenarioMenuOption;
import com.c3po.core.command.Context;
import com.c3po.helper.EmbedHelper;
import com.c3po.model.exploration.*;
import com.c3po.model.pigeon.Pigeon;
import com.c3po.ui.input.base.Menu;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class ScenarioMenu extends Menu {
    public ScenarioMenu(Context context, FullExplorationLocation location, Exploration exploration, Pigeon pigeon) {
        super(context);
        maximumOptionsAllowed = exploration.getActionsRemaining();
        String text = "You arrive at %s (%s).\n\nWhat action would you like to perform?\n";
        embedConsumer = (e -> e
            .description(text.formatted(location.planetName(), location.name()))
            .color(EmbedHelper.COLOR)
            .thumbnail(location.imageUrl()));

        for (ExplorationAction action : location.actions()) {
            addOption(new ScenarioMenuOption(action, pigeon.getGoldModifier()));
        }
    }

    public List<ExplorationScenarioWinnings> getTotalWinnings() {
        return options.values().stream()
            .filter(o -> o instanceof ScenarioMenuOption)
            .map(o->((ScenarioMenuOption) o).getWinnings())
            .filter(Objects::nonNull)
            .collect(Collectors.toList())
        ;
    }
}

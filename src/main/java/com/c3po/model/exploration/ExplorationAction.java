package com.c3po.model.exploration;

import java.util.List;

public record ExplorationAction(int id, String name, String symbol, List<ExplorationScenario> scenarios) {
}

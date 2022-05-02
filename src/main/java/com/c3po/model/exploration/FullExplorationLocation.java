package com.c3po.model.exploration;

import java.util.List;

public record FullExplorationLocation(int id, int planetId, String imageUrl, String planetName, String name, List<ExplorationAction> actions) {
}

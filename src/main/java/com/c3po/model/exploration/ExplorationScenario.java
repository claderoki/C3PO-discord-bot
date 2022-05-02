package com.c3po.model.exploration;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ExplorationScenario {
    private String text;
    private int gold;
    private int health;
    private int happiness;
    private int experience;
    private int cleanliness;
    private int food;
    private Integer itemId;
    private Integer itemCategoryId;
}

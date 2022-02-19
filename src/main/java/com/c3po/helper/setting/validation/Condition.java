package com.c3po.helper.setting.validation;

import com.c3po.annotation.EnumFinder;

@EnumFinder
public enum Condition {
    GT("gt"),
    GTE("gte"),
    LT("lt"),
    LTE("lte");

    private String type;
    Condition(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static Condition find(String type) {
        for(Condition condition: values()) {
            if (condition.getType().equals(type)) {
                return condition;
            }
        }
        throw new RuntimeException("Enum not found.");
    }

}

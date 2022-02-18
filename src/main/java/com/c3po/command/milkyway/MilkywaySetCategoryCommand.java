package com.c3po.command.milkyway;

public class MilkywaySetCategoryCommand extends MilkywayGroup {
    public String getName() {
        return getCategory() + " set category";
    }

    @Override
    public String getValueParameter() {
        return "category";
    }

    @Override
    protected String getSettingKey() {
        return "category_id";
    }
}

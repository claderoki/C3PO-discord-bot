package com.c3po.command.milkyway;

public class MilkywaySetLimitCommand extends MilkywayGroup {
    public String getName() {
        return getCategory() + " set limit";
    }

    public String getValueParameter() {
        return "limit";
    }

    @Override
    protected String getSettingKey() {
        return "active_limit";
    }
}

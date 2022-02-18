package com.c3po.command.milkyway;

public class MilkywaySetCostPerDayCommand extends MilkywayGroup {
    public String getName() {
        return getCategory() + " set costperday";
    }

    public String getValueParameter() {
        return "points";
    }

    @Override
    protected String getSettingKey() {
        return "cost_per_day";
    }
}

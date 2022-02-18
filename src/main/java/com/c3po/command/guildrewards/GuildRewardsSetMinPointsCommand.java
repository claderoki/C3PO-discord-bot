package com.c3po.command.guildrewards;

import java.util.ArrayList;

public class GuildRewardsSetMinPointsCommand extends GuildRewardsGroup {
    public String getName() {
        return getCategory() + " set minpoints";
    }

    public String getValueParameter() {
        return "points";
    }

    @Override
    public String getSettingKey() {
        return "min_points_per_message";
    }

    @Override
    public ArrayList<String> getRequiredSettings() {
        ArrayList<String> settings =  super.getRequiredSettings();
        settings.add("max_points_per_message");
        return settings;
    }
}

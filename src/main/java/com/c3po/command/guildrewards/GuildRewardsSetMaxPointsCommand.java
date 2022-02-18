package com.c3po.command.guildrewards;

import java.util.ArrayList;

public class GuildRewardsSetMaxPointsCommand extends GuildRewardsGroup {
    public String getName() {
        return getCategory() + " set maxpoints";
    }

    public String getValueParameter() {
        return "points";
    }

    @Override
    protected String getSettingKey() {
        return "max_points_per_message";
    }

    @Override
    public ArrayList<String> getRequiredSettings() {
        ArrayList<String> settings =  super.getRequiredSettings();
        settings.add("min_points_per_message");
        return settings;
    }
}

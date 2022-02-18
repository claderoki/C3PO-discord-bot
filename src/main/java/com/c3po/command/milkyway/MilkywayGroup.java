package com.c3po.command.milkyway;

import com.c3po.command.CommandSettings;
import com.c3po.command.SettingGroup;

public abstract class MilkywayGroup extends SettingGroup {
    protected String getCategory() {
        return "milkyway";
    }

    @Override
    public CommandSettings getSettings() {
        return CommandSettings.builder().adminOnly(true).guildOnly(true).build();
    }

}

package com.c3po.command.guildrewards;

import com.c3po.command.CommandSettings;
import com.c3po.command.SettingGroup;

public abstract class GuildRewardsGroup extends SettingGroup {
    protected String getCategory() {
        return "guildrewards";
    }

    @Override
    public CommandSettings getSettings() {
        return CommandSettings.builder().adminOnly(true).guildOnly(true).build();
    }
}

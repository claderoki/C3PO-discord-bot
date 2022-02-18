package com.c3po.command.guildrewards;

import com.c3po.helper.setting.SettingValue;

public class GuildRewardsToggleCommand extends GuildRewardsGroup {
    public String getName() {
        return getCategory() + " toggle";
    }

    @Override
    protected void setValue(SettingValue settingValue, String value) {
        String newValue = settingValue.getValue().equals("0") ? "1" : "0";
        settingValue.setValue(newValue);
    }

    @Override
    protected String getSettingKey() {
        return "enabled";
    }
}

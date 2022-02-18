package com.c3po.command.milkyway;

import com.c3po.helper.setting.SettingValue;

public class MilkywayGodmodeCommand extends MilkywayGroup {
    public String getName() {
        return getCategory() + " godmode";
    }

    @Override
    protected void setValue(SettingValue settingValue, String value) {
        String newValue = settingValue.getValue().equals("0") ? "1" : "0";
        settingValue.setValue(newValue);
    }

    @Override
    protected String getSettingKey() {
        return "godmode";
    }
}

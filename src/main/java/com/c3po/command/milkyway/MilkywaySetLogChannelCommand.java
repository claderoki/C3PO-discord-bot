package com.c3po.command.milkyway;

public class MilkywaySetLogChannelCommand extends MilkywayGroup {
    public String getName() {
        return getCategory() + " set logchannel";
    }

    public String getValueParameter() {
        return "points";
    }

    @Override
    protected String getSettingKey() {
        return "log_channel_id";
    }
}

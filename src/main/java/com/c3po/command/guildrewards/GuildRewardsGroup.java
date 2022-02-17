package com.c3po.command.guildrewards;

import com.c3po.command.Command;
import com.c3po.helper.InteractionHelper;
import com.c3po.helper.setting.Setting;
import com.c3po.helper.setting.SettingCache;
import com.c3po.helper.setting.SettingScopeTarget;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import reactor.core.publisher.Mono;

import java.util.HashMap;

public abstract class GuildRewardsGroup extends Command {

    abstract String getSettingKey();

    protected String getCategory() {
        return "guildrewards";
    }



}

package com.c3po.ui.input;

import com.c3po.core.DataFormatter;
import com.c3po.ui.input.base.SelectMenuMenuOption;
import discord4j.core.event.domain.interaction.SelectMenuInteractionEvent;
import discord4j.core.object.component.*;
import discord4j.core.object.entity.channel.Channel;
import discord4j.core.object.entity.channel.GuildChannel;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.stream.Collectors;

public class ChannelMenuOption extends SelectMenuMenuOption {

    public ChannelMenuOption(String name) {
        super(name);
    }

    @Override
    protected String getPrettyValue() {
        return DataFormatter.prettify(label);
    }

    public SelectMenu getComponent() {
        return SelectMenu.of(getCustomId(), getOptions());
    }

    protected Map<String, String> getOptionCache() {
        return context.getEvent().getClient().getGuildChannels(context.getEvent().getInteraction().getGuildId().orElseThrow())
            .filter(g -> g.getType().equals(Channel.Type.GUILD_TEXT))
            .collect(Collectors.toMap((g) -> g.getId().asString(), GuildChannel::getName)).blockOptional().orElseThrow();
    }

    @Override
    public Mono<?> execute(SelectMenuInteractionEvent event) {
        return super.execute(event).then(event.deferEdit());
    }
}

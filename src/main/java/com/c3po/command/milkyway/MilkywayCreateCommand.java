package com.c3po.command.milkyway;

import com.c3po.command.Command;
import com.c3po.command.CommandSettings;
import com.c3po.command.option.OptionContainer;
import com.c3po.helper.EmbedHelper;
import com.c3po.model.milkyway.Milkyway;
import com.c3po.processors.MilkywayProcessor;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.channel.Channel;
import discord4j.core.spec.EmbedCreateSpec;
import reactor.core.publisher.Mono;

public class MilkywayCreateCommand extends Command {
    @Override
    public String getName() {
        return "milkyway create";
    }

    public CommandSettings getSettings() {
        return CommandSettings.builder().guildOnly(true).build();
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event, OptionContainer options) throws RuntimeException {
        String name = options.getString("name");
        String description = options.optString("description");

        MilkywayProcessor processor = new MilkywayProcessor(event, false);

        event.reply()
            .withEmbeds(EmbedHelper.normal("Working...").build()).block();

        Milkyway milkyway = processor.create(name, description);

        Channel channel = event.getClient().getChannelById(Snowflake.of(processor.getSettings().getLogChannelId())).blockOptional().orElseThrow();
        Member member = event.getInteraction().getMember().orElseThrow();

        String text = "A milkyway has been requested for " + milkyway.getDaysPending() + " day(s)" +
            "\nName: **" + milkyway.getName() + "**" +
            "\nDescription: **" + milkyway.getDescription() + "**";

        String footerText = "Use '/milkyway deny %s' to deny this request.\nUse '/milkyway accept %s' to accept this request.";

        EmbedCreateSpec embed = EmbedHelper.normal(text)
            .footer(footerText.formatted(milkyway.getIdentifier(), milkyway.getIdentifier()), null)
            .author(member.getUsername() + "#" + member.getDiscriminator(), null, member.getAvatarUrl())
            .build();

        return channel.getRestChannel().createMessage(embed.asRequest()).then(
            event.editReply().withEmbeds(EmbedHelper.normal("OK, request has been sent to the admins. Your Milkyway ID is " + milkyway.getIdentifier()).build()).then()
        );
    }
}

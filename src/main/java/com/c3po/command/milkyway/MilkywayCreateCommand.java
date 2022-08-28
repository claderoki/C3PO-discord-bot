package com.c3po.command.milkyway;

import com.c3po.core.command.*;
import com.c3po.helper.DiscordCommandOptionType;
import com.c3po.helper.EmbedHelper;
import com.c3po.processors.MilkywayProcessor;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Member;
import discord4j.core.spec.EmbedCreateSpec;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class MilkywayCreateCommand extends MilkywaySubCommand {
    protected MilkywayCreateCommand() {
        super("create", "Create a milkyway");
        this.addOption(option -> option.name("name")
            .description("The name")
            .required(true)
            .type(DiscordCommandOptionType.STRING.getValue()));
        this.addOption(option -> option.name("description")
            .description("The description")
            .required(false)
            .type(DiscordCommandOptionType.STRING.getValue()));
    }

    public CommandSettings getSettings() {
        return CommandSettings.builder().guildOnly(true).build();
    }

    @Override
    public Mono<Void> execute(Context context) throws RuntimeException {
        String name = context.getOptions().getString("name");
        String description = context.getOptions().optString("description");

        MilkywayProcessor processor = new MilkywayProcessor(context.getEvent(), false);
        beanFactory.autowireBean(processor);

        return context.getEvent().reply().withEmbeds(EmbedHelper.normal("Working...").build())
            .then(processor.create(name, description).flatMap(milkyway -> context.getEvent().getClient().getChannelById(Snowflake.of(processor.getSettings().getLogChannelId())).flatMap(channel -> {
                Member member = context.getEvent().getInteraction().getMember().orElseThrow();
                String text = "A milkyway has been requested for " + milkyway.getDaysPending() + " day(s)" +
                    "\nName: **" + milkyway.getName() + "**" +
                    "\nDescription: **" + milkyway.getDescription() + "**";

                String footerText = "Use '/milkyway deny %s' to deny this request.\nUse '/milkyway accept %s' to accept this request.";
                EmbedCreateSpec embed = EmbedHelper.normal(text)
                    .footer(footerText.formatted(milkyway.getIdentifier(), milkyway.getIdentifier()), null)
                    .author(member.getUsername() + "#" + member.getDiscriminator(), null, member.getAvatarUrl())
                    .build();

                return channel.getRestChannel().createMessage(embed.asRequest()).then(
                    context.getEvent().editReply().withEmbeds(EmbedHelper.normal("OK, request has been sent to the admins. Your Milkyway ID is " + milkyway.getIdentifier()).build()).then()
                );
            })));

    }
}

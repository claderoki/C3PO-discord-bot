package com.c3po.command.milkyway;

import com.c3po.core.command.CommandGroup;
import com.c3po.core.command.CommandSettings;
import com.c3po.core.command.Context;
import com.c3po.error.PublicException;
import com.c3po.helper.DiscordCommandOptionType;
import com.c3po.model.milkyway.Milkyway;
import com.c3po.model.milkyway.MilkywayStatus;
import com.c3po.processors.MilkywayProcessor;
import discord4j.common.util.Snowflake;
import discord4j.discordjson.json.ChannelModifyRequest;
import reactor.core.publisher.Mono;

public class MilkywayExtendCommand extends MilkywaySubCommand {
    protected MilkywayExtendCommand(CommandGroup group) {
        super(group, "extend", "Extend an existing milkyway channel");
        this.addOption(option -> option.name("channel")
            .description("Milkyway channel")
            .required(true)
            .type(DiscordCommandOptionType.CHANNEL.getValue()));
    }

    public CommandSettings getSettings() {
        return CommandSettings.builder().guildOnly(true).build();
    }

    @Override
    public Mono<Void> execute(Context context) throws RuntimeException {
        Snowflake channelId = context.getOptions().getSnowflake("channel");
        return context.getEvent().reply().withContent("abc").then(context.getEvent().getClient().getChannelById(channelId).flatMap(channel -> {
            Milkyway milkyway = milkywayRepository.getFromChannelId(context.getEvent().getInteraction().getGuildId().orElseThrow(), channelId);
            if (milkyway == null) {
                throw new PublicException("Milkyway not found.");
            }
            if (!milkyway.getStatus().equals(MilkywayStatus.ACCEPTED)) {
                throw new PublicException("Milkyway not accepted.");
            }

            MilkywayProcessor processor = new MilkywayProcessor(context.getEvent(), false);
            return processor.extend(milkyway)
                .flatMap(expiresAt -> channel.getRestChannel().modify(ChannelModifyRequest.builder()
                    .topic(MilkywayHelper.getChannelDescriptionFor(milkyway, expiresAt))
                    .build(), "new expiry date"))
                .then(context.getEvent().createFollowup().withContent("OK, extended"))
                .then();
        }));
    }
}

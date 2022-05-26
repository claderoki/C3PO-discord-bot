package com.c3po.command.milkyway;

import com.c3po.core.command.CommandGroup;
import com.c3po.core.command.Context;
import com.c3po.connection.repository.MilkywayRepository;
import com.c3po.core.command.SubCommand;
import com.c3po.errors.PublicException;
import com.c3po.core.ScopeTarget;
import com.c3po.helper.DiscordCommandOptionType;
import com.c3po.model.milkyway.Milkyway;
import com.c3po.model.milkyway.MilkywaySettings;
import com.c3po.model.milkyway.MilkywayStatus;
import com.c3po.service.MilkywayService;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.spec.TextChannelCreateSpec;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class MilkywayAcceptCommand extends MilkywaySubCommand {
    protected MilkywayAcceptCommand(CommandGroup group) {
        super(group, "accept", "Accept a milkyway.");
        this.addOption(option -> option.name("id")
            .description("The identifier to accept")
            .required(true)
            .type(DiscordCommandOptionType.INTEGER.getValue()));
    }

    public Mono<Void> execute(Context context) throws RuntimeException {
        long guildId = context.getEvent().getInteraction().getGuildId().orElseThrow().asLong();
        long identifier = context.getOptions().getLong("id");

        Milkyway milkyway = milkywayRepository.get(guildId, identifier);
        if (milkyway.getStatus() == null) {
            throw new PublicException("This milkyway does not exist.");
        } else if (!milkyway.getStatus().equals(MilkywayStatus.PENDING)) {
            throw new PublicException("This milkyway can't be accepted anymore.");
        }

        MilkywaySettings settings = milkywayService.getSettings(ScopeTarget.guild(guildId));
        return context.getEvent().getInteraction().getGuild().flatMap(guild -> {
            LocalDateTime expiresAt = OffsetDateTime.now(ZoneOffset.UTC).toLocalDateTime().plus(Duration.ofDays(milkyway.getDaysPending()));
            return guild.createTextChannel(TextChannelCreateSpec.builder()
                .name(milkyway.getName())
                .topic(MilkywayHelper.getChannelDescriptionFor(milkyway, expiresAt))
                .parentId(Snowflake.of(settings.getCategoryId()))
                .build()).flatMap(channel -> {
                    milkywayRepository.accept(guildId, identifier, channel.getId().asLong(), expiresAt);

                    return context.getEvent().getInteraction().getUser().getPrivateChannel().flatMap((c) -> c.createMessage(
                        "Your milkyway request has been accepted."
                    ).then(context.getEvent().reply().withContent("OK.")));
            });
        });
    }
}

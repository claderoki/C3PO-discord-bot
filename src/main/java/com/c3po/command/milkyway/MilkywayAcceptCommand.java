package com.c3po.command.milkyway;

import com.c3po.command.Command;
import com.c3po.command.option.OptionContainer;
import com.c3po.connection.repository.MilkywayRepository;
import com.c3po.errors.PublicException;
import com.c3po.helper.setting.SettingScopeTarget;
import com.c3po.model.milkyway.Milkyway;
import com.c3po.model.milkyway.MilkywaySettings;
import com.c3po.model.milkyway.MilkywayStatus;
import com.c3po.service.MilkywayService;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.spec.TextChannelCreateSpec;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class MilkywayAcceptCommand extends Command {
    @Override
    public String getName() {
        return "milkyway accept";
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event, OptionContainer options) throws RuntimeException {
        long guildId = event.getInteraction().getGuildId().orElseThrow().asLong();
        long identifier = options.getLong("id");

        Milkyway milkyway = MilkywayRepository.db().get(guildId, identifier);
        if (milkyway.getStatus() == null) {
            throw new PublicException("This milkyway does not exist.");
        } else if (!milkyway.getStatus().equals(MilkywayStatus.PENDING)) {
            throw new PublicException("This milkyway can't be accepted anymore.");
        }

        MilkywaySettings settings = MilkywayService.getSettings(SettingScopeTarget.guild(guildId));
        Guild guild = event.getInteraction().getGuild().blockOptional().orElseThrow();

        LocalDateTime expiresAt = OffsetDateTime.now(ZoneOffset.UTC).toLocalDateTime().plus(Duration.ofDays(milkyway.getDaysPending()));
        TextChannel channel = guild.createTextChannel(TextChannelCreateSpec.builder()
            .name(milkyway.getName())
            .topic(MilkywayHelper.getChannelDescriptionFor(milkyway, expiresAt))
            .parentId(Snowflake.of(settings.getCategoryId()))
            .build()).blockOptional().orElseThrow();

        MilkywayRepository.db().accept(guildId, identifier, channel.getId().asLong(), expiresAt);

        event.getInteraction().getUser().getPrivateChannel().subscribe((c) -> c.createMessage(
            "Your milkyway request has been accepted."
        ).then());

        return event.reply().withContent("OK.").then();
    }
}

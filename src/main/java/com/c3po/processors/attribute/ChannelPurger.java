package com.c3po.processors.attribute;

import com.c3po.error.ThresholdFailed;
import com.c3po.helper.LogHelper;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.Channel;
import discord4j.discordjson.Id;
import discord4j.discordjson.json.ChannelData;
import discord4j.rest.entity.RestChannel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ChannelPurger extends Task {
    private record SimpleMessage(Snowflake id, Snowflake userId) {
        public SimpleMessage(Id id, Id userId) {
            this(Snowflake.of(id), Snowflake.of(userId));
        }
    }

    private Flux<SimpleMessage> getMessages(RestChannel channel) {
        return channel.getData().map(ChannelData::lastMessageId)
            .map(d -> d.toOptional().flatMap(Function.identity()))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(Snowflake::of)
            .flux()
            .flatMap(i -> channel.getMessagesBefore(i)
                .mergeWith(channel.getRestMessage(i).getData())
                .map(m -> new SimpleMessage(m.id(), m.author().id())))
            ;
    }

    private void thresholdCheck(int before, int after) throws ThresholdFailed {
        int limit = before / 3;
        if (after > limit) {
            throw new ThresholdFailed(before, after);
        }
    }

    private Mono<Integer> deleteMessages(Guild guild, List<SimpleMessage> messages, RestChannel channel) {
        return guild.requestMembers(messages.stream().map(c -> c.userId).collect(Collectors.toSet()))
            .map(User::getId)
            .collectList()
            .map(c -> messages.stream().filter(m -> !c.contains(m.userId)).toList())
            .flatMap(m -> {
                try {
                    thresholdCheck(messages.size(), m.size());
                } catch (ThresholdFailed thresholdFailed) {
                    return Mono.error(thresholdFailed);
                }
                return Flux.fromIterable(m)
                    .map(mm -> channel.getRestMessage(mm.id))
                    .flatMap(mm -> mm.delete("Member left the server.").then(Mono.just(1)))
                    .collectList()
                    .map(List::size);
            });
    }

    private Mono<Integer> executeForChannel(Guild guild, RestChannel channel) {
        return getMessages(channel)
            .collectList()
            .flatMap(m -> deleteMessages(guild, m, channel));
    }

    public Mono<Integer> executeForGuild(Guild guild) {
        return guild.getChannelById(Snowflake.of(729909501578182747L))
            .map(Channel::getRestChannel)
            .flatMap(c -> executeForChannel(guild, c));
    }

    public Mono<Void> execute(GatewayDiscordClient client) {
        return client.getGuilds()
            .filter(c -> c.getId().equals(Snowflake.of(729843647347949638L)))
            .flatMap(this::executeForGuild)
            .filter(c -> c > 0)
            .doOnNext(i -> LogHelper.log(i + " introduction(s) purged"))
            .then();
    }

    @Override
    public Duration getDelay() {
        return Duration.ofHours(1);
    }
}

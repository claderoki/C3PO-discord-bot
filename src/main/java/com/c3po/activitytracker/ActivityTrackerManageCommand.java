package com.c3po.activitytracker;

import com.c3po.connection.repository.AttributeRepository;
import com.c3po.core.Scope;
import com.c3po.core.attribute.KnownAttribute;
import com.c3po.core.command.Context;
import com.c3po.core.property.AttributeCondition;
import com.c3po.helper.DateTimeHelper;
import com.c3po.helper.DiscordCommandOptionType;
import com.c3po.helper.EmbedHelper;
import com.c3po.model.guildreward.ActivityTrackerSettings;
import com.c3po.service.ActivityTrackerService;
import com.c3po.service.AttributeService;
import com.c3po.ui.input.base.ConfirmMenu;
import com.c3po.ui.input.base.MenuManager;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ActivityTrackerManageCommand extends ActivityTrackerSubCommand {
    private final AttributeRepository attributeRepository;
    private final AttributeService attributeService;
    private final ActivityTrackerService activityTrackerService;

    private final static boolean TEST_MODE = true;

    protected ActivityTrackerManageCommand(AttributeRepository attributeRepository, AttributeService attributeService, ActivityTrackerService activityTrackerService) {
        super("manage", "Manage activity in this server.");
        addOption(o -> o.type(DiscordCommandOptionType.INTEGER.getValue())
            .name("days")
            .minValue(1D)
            .maxValue(99D)
            .description("The amount of days.")
            .required(false));
        this.attributeRepository = attributeRepository;
        this.attributeService = attributeService;
        this.activityTrackerService = activityTrackerService;
    }

    private Flux<InactiveMember> getInactiveMembers(Guild guild, Duration cutOffDuration) {
        return Flux.fromStream(attributeRepository.queryCondition(
                    guild.getId().asLong(),
                    attributeService.getId(KnownAttribute.lastActive),
                    AttributeCondition.LTE,
                    DateTimeHelper.now().minus(cutOffDuration).format(DateTimeHelper.DATETIME_FORMATTER)
                )
                .entrySet()
                .stream()
                .map(c -> guild.getMemberById(Snowflake.of(c.getKey()))
                    .map(m -> new InactiveMember(m, LocalDateTime.parse(c.getValue(), DateTimeHelper.DATETIME_FORMATTER)))))
            .flatMap(Function.identity());
    }

    private Mono<Boolean> shouldKick(Context context, List<InactiveMember> members) {
        String message = members.stream()
            .map(m -> m.getMember().getUsername() + ", " + m.getLastActive().format(DateTimeHelper.DATE_FORMATTER)).collect(Collectors.joining("\n"));

        ConfirmMenu menu = new ConfirmMenu(context);
        menu.setEmbedConsumer(e -> e.description(message));
        return new MenuManager<>(menu).waitFor().map(ConfirmMenu::getConfirmed);
    }

    private Mono<Void> kickMember(Member member) {
        if (TEST_MODE) {
            return Mono.empty();
        }
        return member.kick("Inactivity");
    }

    private Mono<Void> kickMembers(Context context, List<InactiveMember> members) {
        return Flux.fromStream(members.stream().map(m -> kickMember(m.getMember())))
        .count()
        .flatMap(i -> context.getInteractor().followup(f -> f.withEmbeds(EmbedHelper.normal(i + " inactives eliminated.").build())))
        .then();
    }

    @Override
    public Mono<Void> execute(Context context) throws RuntimeException {
        Long daysToBeInactive = context.getOptions().optLong("days");
        if (daysToBeInactive == null) {
            ActivityTrackerSettings settings = activityTrackerService.getSettings(context.getTarget().convert(Scope.GUILD).orElseThrow());
            daysToBeInactive = (long) settings.getDaysToBeInactive();
        }

        Duration cutOffDuration = Duration.ofDays(daysToBeInactive);

        return context.getEvent().getInteraction().getGuild()
            .flux()
            .flatMap(g -> getInactiveMembers(g, cutOffDuration))
            .collectList()
            .flatMap(m -> {
                if (m.isEmpty()) {
                    return context.getInteractor().reply(f -> f.withEmbeds(EmbedHelper.normal("No inactives eliminated.").build()))
                        .then(Mono.empty());
                }
                return Mono.just(m);
            })
            .flatMap(m -> shouldKick(context, m)
                .flatMap(shouldKick -> {
                    if (shouldKick) {
                        return kickMembers(context, m);
                    }
                    return context.getInteractor().followup(f -> f.withEmbeds(EmbedHelper.normal("No inactives eliminated.").build()));
                }))
            .then();
    }
}

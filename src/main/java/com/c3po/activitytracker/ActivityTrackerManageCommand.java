package com.c3po.activitytracker;

import com.c3po.core.Scope;
import com.c3po.core.SimpleMessage;
import com.c3po.core.command.Context;
import com.c3po.helper.DateTimeHelper;
import com.c3po.helper.DiscordCommandOptionType;
import com.c3po.helper.EmbedHelper;
import com.c3po.model.guildreward.ActivityTrackerSettings;
import com.c3po.service.ActivityTrackerService;
import com.c3po.ui.input.base.ConfirmMenu;
import com.c3po.ui.input.base.MenuManager;
import discord4j.core.object.entity.Member;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ActivityTrackerManageCommand extends ActivityTrackerSubCommand {
    private final ActivityTrackerService activityTrackerService;

    private final static boolean TEST_MODE = true;

    protected ActivityTrackerManageCommand(ActivityTrackerService activityTrackerService) {
        super("manage", "Manage activity in this server.");
        addOption(o -> o.type(DiscordCommandOptionType.INTEGER.getValue())
            .name("days")
            .minValue(1D)
            .maxValue(99D)
            .description("The amount of days.")
            .required(false));
        this.activityTrackerService = activityTrackerService;
    }

    private Mono<Boolean> shouldKick(Context context, List<InactiveMember> members) {
        if (members.isEmpty()) {
            return Mono.empty();
        }
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
        .flatMap(c -> c)
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
            .flatMap(g -> activityTrackerService.getInactiveMembers(g, cutOffDuration))
            .collectList()
            .flatMap(m -> shouldKick(context, m)
                .flatMap(shouldKick -> {
                    if (shouldKick) {
                        return kickMembers(context, m);
                    }
                    return context.getInteractor().replyOrFollowup(SimpleMessage.builder()
                        .embed(EmbedHelper.normal("No inactives eliminated.").build())
                        .build());
                }))
            .then();
    }
}

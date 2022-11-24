package com.c3po.activitytracker;

import com.c3po.connection.repository.AttributeRepository;
import com.c3po.core.attribute.KnownAttribute;
import com.c3po.core.command.Context;
import com.c3po.core.property.AttributeCondition;
import com.c3po.helper.DateTimeHelper;
import com.c3po.helper.EmbedHelper;
import com.c3po.helper.cache.CacheManager;
import com.c3po.service.AttributeService;
import com.c3po.ui.input.base.ConfirmMenu;
import com.c3po.ui.input.base.MenuManager;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Guild;
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

    protected ActivityTrackerManageCommand(AttributeRepository attributeRepository, AttributeService attributeService) {
        super("manage", "Manage activity in this server.");
        this.attributeRepository = attributeRepository;
        this.attributeService = attributeService;
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

    private Mono<Boolean> confirm(Context context, List<InactiveMember> members) {
        String message = members.stream()
            .map(m -> m.getMember().getUsername() + ", " + m.getLastActive().format(DateTimeHelper.DATE_FORMATTER)).collect(Collectors.joining("\n"));

        ConfirmMenu menu = new ConfirmMenu(context);
        menu.setEmbedConsumer(e -> e.description(message));
        return new MenuManager<>(menu).waitFor().map(ConfirmMenu::getConfirmed);
    }

    @Override
    public Mono<Void> execute(Context context) throws RuntimeException {
        CacheManager.removeAllExpiredItems();
        Duration cutOffDuration = Duration.ofDays(2);
        return context.getEvent().getInteraction().getGuild()
            .flux()
            .flatMap(g -> getInactiveMembers(g, cutOffDuration))
            .collectList()
            .flatMap(c -> confirm(context, c)
                .flatMap(confirmed -> {
                    if (confirmed) {
                        return context.getInteractor().followup(f -> f.withEmbeds(EmbedHelper.normal("all inactives eliminated.").build()));
//                        return Flux.fromStream(c.stream().map(m -> m.getMember().kick("Inactive for %s or more days".formatted(cutOffDuration.toDays()))))
//                            .count()
//                            .map(i -> context.getInteractor().followup(f -> f.withEmbeds(EmbedHelper.normal(i + " inactives eliminated.").build())))
//                            .then();
                    }
                    return context.getInteractor().followup(f -> f.withEmbeds(EmbedHelper.normal("No inactives eliminated.").build()));
                }))
            .then();
    }
}

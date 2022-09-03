package com.c3po.command.pigeon;

import com.c3po.command.pigeon.validation.PigeonValidationResult;
import com.c3po.command.pigeon.validation.PigeonValidationSettings;
import com.c3po.connection.repository.ExplorationRepository;
import com.c3po.connection.repository.ReminderRepository;
import com.c3po.core.command.Context;
import com.c3po.helper.DateTimeHelper;
import com.c3po.helper.RandomHelper;
import com.c3po.model.exploration.FullExplorationLocation;
import com.c3po.model.pigeon.PigeonStatus;
import com.c3po.model.reminder.NewReminder;
import com.c3po.service.ExplorationService;
import com.c3po.ui.input.VoidMenuOption;
import com.c3po.ui.input.base.Menu;
import com.c3po.ui.input.base.MenuManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;

@Component
public class PigeonExploreCommand extends PigeonSubCommand {
    @Autowired
    protected ExplorationRepository explorationRepository;
    @Autowired
    protected ReminderRepository reminderRepository;
    @Autowired
    protected ExplorationService explorationService;

    protected PigeonExploreCommand() {
        super("explore", "no description.");
    }

    protected PigeonValidationSettings getValidationSettings() {
        return PigeonValidationSettings.builder()
            .needsActivePigeon(true)
            .requiredPigeonStatus(PigeonStatus.IDLE)
            .build();
    }

    @Override
    public Mono<Void> execute(Context context) throws RuntimeException {
        long userId = context.getEvent().getInteraction().getUser().getId().asLong();

        PigeonValidationSettings settings = getValidationSettings();
        PigeonValidationResult result = validation.validate(settings, userId);

        FullExplorationLocation location = RandomHelper.choice(explorationService.getAllLocations().values().stream().toList());

        LocalDateTime arrivalDate = DateTimeHelper.now().plus(Duration.ofMinutes(90));
        explorationRepository.createExploration(location.id(), arrivalDate, result.getPigeonId());
        pigeonRepository.updateStatus(result.getPigeonId(), PigeonStatus.SPACE_EXPLORING);

        Menu menu = new Menu(context);
        menu.setEmbedConsumer(c -> c
            .description("Your pigeon has successfully taken off to space!")
            .thumbnail(location.imageUrl()));
        VoidMenuOption option = new VoidMenuOption("Remind me!");
        option.setShouldContinue(false);
        option.withEmoji("❗");
        option.setExecutor((e) -> {
            NewReminder reminder = new NewReminder(
                userId,
                context.getEvent().getInteraction().getChannelId().asLong(),
                "Your pigeon has landed on Luna!\n`/pigeon space` to check on it!",
                arrivalDate
            );
            reminderRepository.create(reminder);
            return e.createFollowup().withContent("Okay, I will remind you when your pigeon has arrived.").then();
        });
        menu.addOption(option);

        return new MenuManager<>(menu).waitFor().then();
    }
}

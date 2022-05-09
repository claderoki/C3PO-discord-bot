package com.c3po.command.pigeon;

import com.c3po.connection.repository.ExplorationRepository;
import com.c3po.connection.repository.PigeonRepository;
import com.c3po.connection.repository.ReminderRepository;
import com.c3po.core.command.Context;
import com.c3po.helper.DateTimeHelper;
import com.c3po.helper.RandomHelper;
import com.c3po.model.exploration.FullExplorationLocation;
import com.c3po.model.exploration.SimplePlanetLocation;
import com.c3po.model.pigeon.PigeonStatus;
import com.c3po.model.reminder.NewReminder;
import com.c3po.service.ExplorationService;
import com.c3po.ui.input.VoidMenuOption;
import com.c3po.ui.input.base.Menu;
import com.c3po.ui.input.base.MenuManager;
import reactor.core.publisher.Mono;

import javax.xml.stream.Location;
import java.time.Duration;
import java.time.LocalDateTime;

public class PigeonExploreCommand extends PigeonSubCommand {
    protected PigeonExploreCommand(PigeonCommandGroup group) {
        super(group, "explore", "no description.");
    }

    protected PigeonValidation getValidation() {
        return PigeonValidation.builder()
            .needsActivePigeon(true)
            .requiredPigeonStatus(PigeonStatus.IDLE)
            .build();
    }

    @Override
    public Mono<?> execute(Context context) throws RuntimeException {
        long userId = context.getEvent().getInteraction().getUser().getId().asLong();

        PigeonValidation validation = getValidation();
        PigeonValidationResult result = validation.validate(userId);

        FullExplorationLocation location = RandomHelper.choice(ExplorationService.getAllLocations().values().stream().toList());

        LocalDateTime arrivalDate = DateTimeHelper.now().plus(Duration.ofMinutes(90));
        ExplorationRepository.db().createExploration(location.id(), arrivalDate, result.getPigeonId());
        PigeonRepository.db().updateStatus(result.getPigeonId(), PigeonStatus.SPACE_EXPLORING);

        Menu menu = new Menu(context);
        menu.setEmbedConsumer(c -> c
            .description("Your pigeon has successfully taken off to space!")
            .thumbnail(location.imageUrl()));
        VoidMenuOption option = new VoidMenuOption("Remind me!");
        option.withEmoji("❗");
        option.setExecutor((e) -> {
            NewReminder reminder = new NewReminder(
                userId,
                context.getEvent().getInteraction().getChannelId().asLong(),
                "Your pigeon has landed on Luna!\n`/pigeon space` to check on it!",
                arrivalDate
            );
            ReminderRepository.db().create(reminder);
            return e.createFollowup().withContent("Okay, I will remind you when your pigeon has arrived.");
        });
        menu.addOption(option);

        MenuManager.waitForMenu(menu).blockOptional();

        return Mono.empty();
    }
}
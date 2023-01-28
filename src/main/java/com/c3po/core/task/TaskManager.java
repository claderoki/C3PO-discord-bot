package com.c3po.core.task;

import com.c3po.DiscordInfo;
import com.c3po.connection.repository.TaskRepository;
import com.c3po.helper.DateTimeHelper;
import com.c3po.helper.LogHelper;
import com.c3po.processors.attribute.Task;
import com.c3po.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@RequiredArgsConstructor
public class TaskManager {
    private final TaskRepository taskRepository;
    private final TaskService taskService;

    private final DiscordInfo discordInfo;

    private Mono<Optional<Duration>> getInitialDelay(String identifier, Duration delay) {
        return taskService.getTasks()
            .map(c -> c.getOrDefault(identifier, DateTimeHelper.now().minus(Duration.ofHours(48))))
            .map(c -> ChronoUnit.SECONDS.between(c, DateTimeHelper.now()))
            .filter(c -> c < delay.getSeconds())
            .map(Duration::ofSeconds)
            .map(delay::minus)
            .map(Optional::of)
            .defaultIfEmpty(Optional.empty());
    }

    private Mono<Void> afterTask(String identifier) {
        return taskRepository.updateTask(identifier, DateTimeHelper.now());
    }

    private Mono<Void> executeTask(Mono<Void> mono, String identifier, @Nullable Duration delay) {
        if (delay == null) {
            delay = Duration.ofSeconds(0);
            LogHelper.log("Executing " + identifier + " with no delay");
        } else {
            LogHelper.log("Executing " + identifier + " with a " + delay.toMinutes() + " minutes delay.");
        }

        return Mono.delay(delay)
            .then(mono)
            .then(afterTask(identifier));
    }

    private Flux<Void> createTask(Mono<Void> mono, String identifier, Duration fixedDelay) {
        Optional<Duration> initialDelay = getInitialDelay(identifier, fixedDelay).block();
        AtomicInteger i = new AtomicInteger();
        return Mono.defer(() -> {
                Duration delay;
                if (i.getAndIncrement() > 0) {
                    delay = fixedDelay;
                } else {
                    delay = initialDelay.orElse(null);
                }
                return executeTask(mono, identifier, delay);
            })
            .repeat();
    }

    public void register(Task task) {
        createTask(task.execute(discordInfo.getClient()), task.getIdentifier(), task.getDelay())
            .onErrorContinue((e,o) -> LogHelper.log(e, "Task " + task.getIdentifier()))
            .subscribe();
    }
}

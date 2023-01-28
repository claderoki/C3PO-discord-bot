package com.c3po.connection.repository;

import com.c3po.connection.Repository;
import com.c3po.connection.query.Query;
import com.c3po.database.DateTimeParameter;
import com.c3po.database.StringParameter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskRepository extends Repository {


    public Mono<Map<String, LocalDateTime>> getTasks() {
        return fluxMany(new Query("SELECT * FROM `task`"))
            .collectList()
            .map(t -> t.stream().collect(Collectors.toMap(
                c -> c.getString("identifier"),
                c -> c.getDateTime("last_run")))
            );
    }

    public Mono<Void> updateTask(String identifier, LocalDateTime lastRan) {
        return monoExecute("INSERT INTO `task` (`identifier`, `last_run`) VALUES (?, ?) ON DUPLICATE KEY UPDATE `last_run` = ?",
            new StringParameter(identifier),
            new DateTimeParameter(lastRan),
            new DateTimeParameter(lastRan)
        ).then();
    }
}
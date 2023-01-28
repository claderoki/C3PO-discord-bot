package com.c3po.service;

import com.c3po.connection.repository.TaskRepository;
import com.c3po.helper.cache.CacheManager;
import com.c3po.helper.cache.keys.TaskRunsKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;

    public Mono<Map<String, LocalDateTime>> getTasks() {
        return CacheManager.get().computeIfAbsentMono(new TaskRunsKey(), c -> taskRepository.getTasks());
    }
}

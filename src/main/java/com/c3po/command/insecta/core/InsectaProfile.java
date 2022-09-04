package com.c3po.command.insecta.core;

import java.time.LocalDateTime;

public record InsectaProfile(Integer id, long hexacoin, LocalDateTime lastCollected, Insectarium insectarium) {
}

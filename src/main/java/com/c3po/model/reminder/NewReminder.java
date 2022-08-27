package com.c3po.model.reminder;

import java.time.LocalDateTime;

public record NewReminder(long userId, Long channelId, String message, LocalDateTime dueDate) {
}

package com.c3po.core.wordnik;

import com.c3po.core.api.ApiCall;
import com.c3po.core.api.ApiEndpoint;
import com.c3po.helper.environment.Configuration;
import lombok.NonNull;

import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

public class WordnikApi extends ApiCall {
    @Override
    public <E extends ApiEndpoint<?>> String getBaseUri(E endpoint) {
        return "https://api.wordnik.com/v4";
    }

    @Override
    public Map<String, String> getDefaultParameters() {
        return Map.of("api_key", Configuration.instance().getWordnikKey());
    }

    @Override
    protected boolean shouldRetry(HttpResponse<String> response) {
        return response.statusCode() == 429;
    }

    @Override
    protected @NonNull Duration getThrottleDuration(HttpResponse<String> response) {
        long totalMinute = response.headers().firstValueAsLong("x-ratelimit-limit-minute").orElseThrow();
        long remainingMinute = response.headers().firstValueAsLong("x-ratelimit-remaining-minute").orElseThrow();
        long remainingHour = response.headers().firstValueAsLong("x-ratelimit-remaining-hour").orElseThrow();

        if (remainingMinute < 3) {
            return Duration.ofSeconds(20);
        }
        if (remainingHour == 0) {
            throw new RuntimeException("Out of api calls");
        }
        return Duration.ZERO;
    }
}

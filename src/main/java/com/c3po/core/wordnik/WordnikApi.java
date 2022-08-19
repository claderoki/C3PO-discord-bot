package com.c3po.core.wordnik;

import com.c3po.core.api.ApiCall;
import com.c3po.core.api.ApiEndpoint;
import com.c3po.helper.environment.Configuration;
import lombok.NonNull;
import reactor.netty.http.client.HttpClientResponse;

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
    protected boolean shouldRetry(HttpClientResponse response) {
        return response.status().code() == 429;
    }

    @Override
    protected boolean validate(HttpClientResponse response) {
        int remainingHour = response.responseHeaders().getInt("x-ratelimit-remaining-hour");
        return super.validate(response) && remainingHour > 0;
    }

    @Override
    protected @NonNull Duration getThrottleDuration(HttpClientResponse response) {
        int remainingMinute = response.responseHeaders().getInt("x-ratelimit-remaining-minute");
        if (remainingMinute < 3) {
            return Duration.ofSeconds(20);
        }
        return Duration.ZERO;
    }
}

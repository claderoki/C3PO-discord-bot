package com.c3po.core.api;

import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class ApiCall {
    public abstract <E extends ApiEndpoint<?>> String getBaseUri(E endpoint);

    private <E extends ApiEndpoint<?>> Map<String, String> getAllParameters(E endpoint) {
        Map<String, String> defaultParameters = getDefaultParameters();
        Map<String, String> endpointParameters = endpoint.getParameters();

        if (defaultParameters == null && endpointParameters == null) {
            return null;
        }
        if (defaultParameters != null && endpointParameters != null) {
            Map<String, String> parameters = new LinkedHashMap<>(defaultParameters);
            parameters.putAll(endpointParameters);
            return Collections.unmodifiableMap(parameters);
        }
        return (defaultParameters == null ? endpointParameters : defaultParameters);
    }

    private <T extends ApiResponse, E extends ApiEndpoint<T>> HttpRequest prepareCall(E endpoint) {
        String rawParameters = null;
        Map<String, String> parameters = getAllParameters(endpoint);
        if (parameters != null && !parameters.isEmpty()) {
            rawParameters = parameters.entrySet().stream().map((c) -> c.getKey() + "=" + c.getValue()).collect(Collectors.joining("&"));
        }

        var uri = getBaseUri(endpoint) + "/" + endpoint.getEndpoint() + (rawParameters != null ? ("?"+rawParameters) : "");

        var builder = HttpRequest.newBuilder();
        switch (endpoint.getMethod()) {
            case GET -> builder.GET();
            case POST -> builder.POST(HttpRequest.BodyPublishers.noBody());
        }

        return builder
            .uri(URI.create(uri))
            .build();
    }

    private <T extends ApiResponse, E extends ApiEndpoint<T>> Mono<T> _call(E endpoint, int retryCount) throws Exception {
        HttpRequest request = prepareCall(endpoint);
        HttpClient client = HttpClient.newBuilder().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (shouldRetry(response) && retryCount < endpoint.getMaxRetries()) {
            return Mono.delay(Duration.ofMinutes(1))
                .then(Mono.defer(() -> {
                    try {
                        return _call(endpoint, retryCount+1);
                    } catch (Exception e) {
                        return Mono.error(e);
                    }
                }));
        }
        if (!validate(response)) {
            throw new FailedCallException(response.statusCode(), response.body());
        }
        Duration throttleDuration = getThrottleDuration(response);
        return Mono.delay(throttleDuration)
            .then(Mono.defer(() -> Mono.just(endpoint.parseResponse(response.body()))));
    }

    public <T extends ApiResponse, E extends ApiEndpoint<T>> Mono<T> call(E endpoint) {
        try {
            return _call(endpoint, 0);
        } catch (Exception e) {
            return Mono.error(e);
        }
    }

    protected Duration getThrottleDuration(HttpResponse<String> response) throws Exception {
        return Duration.ZERO;
    }

    protected boolean shouldRetry(HttpResponse<String> response) {
        return false;
    }

    protected boolean validate(HttpResponse<String> response) {
        return response.statusCode() >= 200 && response.statusCode() < 300;
    }

    public Map<String, String> getDefaultParameters() {
        return null;
    }
}

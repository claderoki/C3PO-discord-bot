package com.c3po.core.api;

import io.netty.handler.codec.http.HttpHeaders;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.ByteBufFlux;
import reactor.netty.http.client.HttpClient;
import reactor.netty.http.client.HttpClientResponse;

import java.time.Duration;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class ApiCall {
    protected abstract <E extends ApiEndpoint<?>> String getBaseUri(E endpoint);

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

    private <T extends ApiResponse, E extends ApiEndpoint<T>> String getFullUri(E endpoint) {
        String rawParameters = null;
        Map<String, String> parameters = getAllParameters(endpoint);
        if (parameters != null && !parameters.isEmpty()) {
            rawParameters = parameters.entrySet().stream().map((c) -> c.getKey() + "=" + c.getValue()).collect(Collectors.joining("&"));
        }
        return getBaseUri(endpoint) + "/" + endpoint.getEndpoint() + (rawParameters != null ? ("?"+rawParameters) : "");
    }

    protected void modifyHeaders(HttpHeaders headers) {

    }

    private <T extends ApiResponse, E extends ApiEndpoint<T>> HttpClient.ResponseReceiver<?> getReceiver(E endpoint, HttpClient.RequestSender sender) {
        var body = endpoint.getBody();
        if (body == null ) {
            return sender.uri(getFullUri(endpoint));
        }
        return sender.send(ByteBufFlux.fromString(Flux.just(body.toString())))
            .uri(getFullUri(endpoint));
    }

    private <T extends ApiResponse, E extends ApiEndpoint<T>> Mono<T> _call(E endpoint, int retryCount) {
        HttpClient client = HttpClient.create()
            .headers(h -> {
                modifyHeaders(h);
                endpoint.modifyHeaders(h);
            });

        var receiver = switch (endpoint.getMethod()) {
            case GET -> client.get().uri(getFullUri(endpoint));
            case POST -> getReceiver(endpoint, client.post());
        };

        return receiver.responseSingle((response, bytes) -> {
            if (shouldRetry(response) && retryCount < endpoint.getMaxRetries()) {
                return Mono.delay(Duration.ofSeconds(60)).then(call(endpoint, retryCount+1));
            }
            Duration throttleDuration = getThrottleDuration(response);
            Mono<String> value = Mono.delay(throttleDuration).then(bytes.asString());
            if (isInvalid(response)) {
                return value.flatMap(c -> Mono.error(new FailedCallException(response.status().code(), c, retryCount)));
            }
            return value.map(endpoint::parseResponse);
        });
    }

    public final <T extends ApiResponse, E extends ApiEndpoint<T>> Mono<T> call(E endpoint) {
        return call(endpoint, 0);
    }

    private <T extends ApiResponse, E extends ApiEndpoint<T>> Mono<T> call(E endpoint, int retryCount) {
        return Mono.defer(() -> _call(endpoint, retryCount));
    }

    protected Duration getThrottleDuration(HttpClientResponse response) {
        return Duration.ZERO;
    }

    protected boolean shouldRetry(HttpClientResponse response) {
        return false;
    }

    protected boolean isInvalid(HttpClientResponse response) {
        return response.status().code() < 200 || response.status().code() >= 300;
    }

    protected Map<String, String> getDefaultParameters() {
        return null;
    }
}

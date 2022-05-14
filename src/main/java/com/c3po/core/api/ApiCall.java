package com.c3po.core.api;

import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
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

    public <T extends ApiResponse, E extends ApiEndpoint<T>> Mono<T> call(E endpoint) throws Exception {
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

        var request = builder
            .uri(URI.create(uri))
            .build();

        HttpClient client = HttpClient.newBuilder().build();

        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return Mono.just(endpoint.parseResponse(response.body()));
    }

    public Map<String, String> getDefaultParameters() {
        return null;
    }
}

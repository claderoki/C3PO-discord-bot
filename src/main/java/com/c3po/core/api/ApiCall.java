package com.c3po.core.api;

import org.json.JSONObject;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class ApiCall {
    public abstract String getBaseUri();

    private <E extends ApiEndpoint<?>> Map<String, String> getAllParameters(E endpoint) {
        Map<String, String> defaultParameters = getDefaultParameters();
        Map<String, String> endpointParameters = endpoint.getParameters();

        if (defaultParameters == null && endpointParameters == null) {
            return null;
        }
        if (defaultParameters != null && endpointParameters != null) {
            Map<String, String> parameters = new HashMap<>(defaultParameters);
            parameters.putAll(endpointParameters);
            return parameters;
        }
        return (defaultParameters == null ? endpointParameters : defaultParameters);
    }

    public <T extends ApiResponse, E extends ApiEndpoint<T>> Mono<T> call(E endpoint) throws IOException, InterruptedException {
        String rawParameters = null;
        Map<String, String> parameters = getAllParameters(endpoint);
        if (parameters != null && !parameters.isEmpty()) {
            rawParameters = parameters.entrySet().stream().map((c) -> c.getKey() + "=" + c.getValue()).collect(Collectors.joining("&"));
        }

        var uri = getBaseUri() + "/" + endpoint.getEndpoint() + (rawParameters != null ? ("?"+rawParameters) : "");
        var request = HttpRequest.newBuilder()
            .GET()
            .uri(URI.create(uri))
            .build();
        HttpClient client = HttpClient.newBuilder().build();

        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return Mono.just(endpoint.parseResponse(new JSONObject(response.body())));
    }

    public Map<String, String> getDefaultParameters() {
        return null;
    }
}

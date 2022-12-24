package com.c3po.core.api;

import io.netty.handler.codec.http.HttpHeaders;
import reactor.netty.http.client.HttpClientForm;

import java.util.Map;

public abstract class ApiEndpoint<T extends ApiResponse> {
    public abstract String getEndpoint();
    public abstract T parseResponse(String rawResponse);
    public Map<String, String> getParameters() {
        return null;
    }
    public HttpMethod getMethod() {
        return HttpMethod.GET;
    }

    public int getMaxRetries() {
        return 0;
    }

    public Object getBody() {
        return null;
    }

    public void modifyHeaders(HttpHeaders headers) {

    }

    /** Note that this method will only get called on POST calls.
     * @param form form
     */
    public void modifyForm(HttpClientForm form) {
    }
}

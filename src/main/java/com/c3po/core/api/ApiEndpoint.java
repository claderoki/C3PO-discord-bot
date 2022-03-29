package com.c3po.core.api;

import org.json.JSONObject;

import java.util.Map;

public abstract class ApiEndpoint<T extends ApiResponse> {
    public abstract String getEndpoint();
    public abstract T parseResponse(JSONObject jsonObject);
    public Map<String, String> getParameters() {
        return null;
    }
    public HttpMethod getMethod() {
        return HttpMethod.GET;
    }
}

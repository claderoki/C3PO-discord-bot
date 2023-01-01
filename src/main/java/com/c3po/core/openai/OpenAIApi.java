package com.c3po.core.openai;

import com.c3po.core.api.ApiCall;
import com.c3po.core.api.ApiEndpoint;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class OpenAIApi extends ApiCall {
    private final String apiKey;

    @Override
    public <E extends ApiEndpoint<?>> String getBaseUri(E endpoint) {
        return "https://api.openai.com/v1";
    }

    @Override
    protected void modifyHeaders(HttpHeaders headers) {
        headers.add(HttpHeaderNames.AUTHORIZATION, "Bearer " + apiKey);
    }

}

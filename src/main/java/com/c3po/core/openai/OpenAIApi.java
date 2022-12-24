package com.c3po.core.openai;

import com.c3po.core.api.ApiCall;
import com.c3po.core.api.ApiEndpoint;
import com.c3po.helper.environment.Configuration;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;

public class OpenAIApi extends ApiCall {
    @Override
    public <E extends ApiEndpoint<?>> String getBaseUri(E endpoint) {
        return "https://api.openai.com/v1";
    }

    @Override
    protected void modifyHeaders(HttpHeaders headers) {
        headers.add(HttpHeaderNames.AUTHORIZATION, "Bearer " + Configuration.instance().getOpenAiKey());
    }

}

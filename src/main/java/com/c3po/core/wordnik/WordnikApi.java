package com.c3po.core.wordnik;

import com.c3po.core.api.ApiCall;
import com.c3po.core.api.ApiEndpoint;
import com.c3po.helper.environment.Configuration;

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
}

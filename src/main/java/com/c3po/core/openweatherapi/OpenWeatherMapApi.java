package com.c3po.core.openweatherapi;

import com.c3po.core.api.ApiCall;
import com.c3po.core.api.ApiEndpoint;
import com.c3po.helper.environment.Configuration;

import java.util.Map;

public class OpenWeatherMapApi extends ApiCall {
    @Override
    public <E extends ApiEndpoint<?>> String getBaseUri(E endpoint) {
        return "https://api.openweathermap.org/data/2.5/weather";
    }

    @Override
    public Map<String, String> getDefaultParameters() {
        return Map.of("appid", Configuration.instance().getOwmKey());
    }
}

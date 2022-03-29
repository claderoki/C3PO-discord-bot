package com.c3po.core.openweatherapi.endpoints;

import com.c3po.core.api.ApiEndpoint;
import com.c3po.core.openweatherapi.responses.Temperature;
import lombok.Builder;
import org.json.JSONObject;

import java.util.Map;

@Builder
public class GetTemperature extends ApiEndpoint<Temperature> {
    private String countryCode;
    private String cityName;

    @Override
    public String getEndpoint() {
        return "";
    }

    @Override
    public Temperature parseResponse(JSONObject jsonObject) {
        return new Temperature(jsonObject);
    }

    @Override
    public Map<String, String> getParameters() {
        return Map.of(
            "q", cityName + (countryCode == null ? "" : ","+countryCode),
            "units", "metric"
        );
    }
}

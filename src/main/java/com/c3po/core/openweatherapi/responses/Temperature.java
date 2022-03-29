package com.c3po.core.openweatherapi.responses;

import com.c3po.core.api.ApiResponse;
import lombok.Getter;
import org.json.JSONObject;

@Getter
public class Temperature extends ApiResponse {
    private final float temperature;
    private final String weatherStatus;

    public Temperature(JSONObject json) {
        super(json);
        temperature = json.getJSONObject("main").getFloat("temp");
        weatherStatus = json.getJSONArray("weather").getJSONObject(0).getString("main");
    }
}

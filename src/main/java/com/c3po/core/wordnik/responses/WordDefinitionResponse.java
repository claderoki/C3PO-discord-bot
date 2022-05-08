package com.c3po.core.wordnik.responses;

import lombok.Getter;
import org.json.JSONObject;

@Getter
public class WordDefinitionResponse {
    private final String text;
    public WordDefinitionResponse(JSONObject response) {
        text = response.optString("text");
    }
}

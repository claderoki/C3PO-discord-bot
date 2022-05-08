package com.c3po.core.wordnik.responses;

import com.c3po.core.api.ApiResponse;
import lombok.Getter;
import org.json.JSONObject;

@Getter
public class WordResponse extends ApiResponse {
    private final String word;

    public WordResponse(JSONObject json) {
        word = json.getString("word");
    }
}

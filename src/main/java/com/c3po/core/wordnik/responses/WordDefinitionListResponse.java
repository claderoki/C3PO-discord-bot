package com.c3po.core.wordnik.responses;

import com.c3po.core.api.ApiResponse;
import lombok.Getter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@Getter
public class WordDefinitionListResponse extends ApiResponse {
    private final List<WordDefinitionResponse> definitions = new ArrayList<>();
    public WordDefinitionListResponse(JSONArray json) {
        for(int i = 0; i < json.length(); i++) {
            definitions.add(new WordDefinitionResponse(json.getJSONObject(i)));
        }
    }
}

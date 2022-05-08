package com.c3po.core.wordnik.endpoints;

import com.c3po.core.api.ApiEndpoint;
import com.c3po.core.wordnik.responses.WordDefinitionListResponse;
import com.c3po.core.wordnik.responses.WordResponse;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;

@RequiredArgsConstructor
public class GetWordDefinition extends ApiEndpoint<WordDefinitionListResponse> {
    private final String word;

    @Override
    public String getEndpoint() {
        return "word.json/" + word + "/definitions";
    }

    @Override
    public WordDefinitionListResponse parseResponse(String rawResponse) {
        return new WordDefinitionListResponse(new JSONArray(rawResponse));
    }

    @Override
    public Map<String, String> getParameters() {
        return Map.of(
            "limit", "1",
            "includeRelated", "false"
        );
    }
}

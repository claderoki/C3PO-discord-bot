package com.c3po.core.wordnik.endpoints;

import com.c3po.core.api.ApiEndpoint;
import com.c3po.core.wordnik.responses.WordDefinitionListResponse;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;

import java.util.Map;

@RequiredArgsConstructor
public class GetWordDefinition extends ApiEndpoint<WordDefinitionListResponse> {
    private final String word;

    @Override
    public int getMaxRetries() {
        return 3;
    }

    @Override
    public String getEndpoint() {
        return "word.json/" + word + "/definitions";
    }

    @Override
    public WordDefinitionListResponse parseResponse(String rawResponse) {
        return new WordDefinitionListResponse(word, new JSONArray(rawResponse));
    }

    @Override
    public Map<String, String> getParameters() {
        return Map.of(
            "limit", "1",
            "includeRelated", "false"
        );
    }
}

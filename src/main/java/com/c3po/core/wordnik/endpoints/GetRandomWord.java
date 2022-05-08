package com.c3po.core.wordnik.endpoints;

import com.c3po.core.api.ApiEndpoint;
import com.c3po.core.wordnik.responses.WordResponse;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;

import java.util.Map;

@RequiredArgsConstructor
public class GetRandomWord extends ApiEndpoint<WordResponse> {
    @Override
    public String getEndpoint() {
        return "words.json/randomWord";
    }

    @Override
    public WordResponse parseResponse(String rawResponse) {
        return new WordResponse(new JSONObject(rawResponse));
    }

    @Override
    public Map<String, String> getParameters() {
        return Map.of(
            "hasDictionaryDef", "true",
            "minLength", "6",
            "minCorpusCount", "100",
            "minDictionaryCount", "5"
        );
    }
}

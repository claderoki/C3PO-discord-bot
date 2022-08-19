package com.c3po.core.wordnik.endpoints;

import com.c3po.core.api.ApiEndpoint;
import com.c3po.core.wordnik.responses.WordListResponse;
import com.c3po.core.wordnik.responses.WordResponse;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;

@RequiredArgsConstructor
@Setter
public class GetRandomWords extends ApiEndpoint<WordListResponse> {
    private int limit = 10;

    @Override
    public int getMaxRetries() {
        return 3;
    }

    @Override
    public String getEndpoint() {
        return "words.json/randomWords";
    }

    @Override
    public WordListResponse parseResponse(String rawResponse) {
        return new WordListResponse(new JSONArray(rawResponse));
    }

    @Override
    public Map<String, String> getParameters() {
        return Map.of(
            "hasDictionaryDef", "true",
            "minLength", "6",
            "minCorpusCount", "100",
            "minDictionaryCount", "5",
            "limit", String.valueOf(limit)
        );
    }
}

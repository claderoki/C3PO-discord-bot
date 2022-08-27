package com.c3po.core.wordnik.responses;

import com.c3po.core.api.ApiResponse;
import lombok.Getter;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

@Getter
public class WordListResponse extends ApiResponse {
    private final List<WordResponse> words = new ArrayList<>();

    public WordListResponse(JSONArray json) {
        for(int i = 0; i < json.length(); i++) {
            words.add(new WordResponse(json.getJSONObject(i)));
        }
    }
}

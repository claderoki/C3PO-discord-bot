package com.c3po.core.openai.responses;

import com.c3po.core.api.ApiResponse;
import lombok.Getter;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class ImagesResponse extends ApiResponse {
    private LocalDateTime createdAt;
    private final List<String> urls;

    public ImagesResponse(JSONObject json) {
        var array = json.getJSONArray("data");

        ArrayList<String> urls = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            var img = array.getJSONObject(i);
            urls.add(img.getString("url"));
        }
        this.urls = Collections.unmodifiableList(urls);
    }
}

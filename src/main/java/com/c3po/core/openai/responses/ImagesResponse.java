package com.c3po.core.openai.responses;

import com.c3po.core.api.ApiResponse;
import lombok.Getter;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class ImagesResponse extends ApiResponse {
    private final List<String> urls;
    private final List<B64Json> b64s;

    public ImagesResponse(JSONObject json) {
        var array = json.getJSONArray("data");

        ArrayList<String> urls = new ArrayList<>();
        ArrayList<B64Json> b64 = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            var data = array.getJSONObject(i);
            if (data.has("url")) {
                urls.add(data.getString("url"));
            } else {
                b64.add(new B64Json(data.getString("b64_json")));
            }
        }
        this.urls = Collections.unmodifiableList(urls);
        this.b64s = Collections.unmodifiableList(b64);
    }
}

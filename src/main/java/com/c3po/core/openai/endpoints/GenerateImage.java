package com.c3po.core.openai.endpoints;

import com.c3po.core.api.ApiEndpoint;
import com.c3po.core.api.HttpMethod;
import com.c3po.core.openai.responses.ImagesResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;

@Builder
public class GenerateImage extends ApiEndpoint<ImagesResponse> {
    @RequiredArgsConstructor
    public
    enum Size {
        small("256x256"),
        medium("512x512"),
        large("1024x1024");

        private final String value;

        @Override
        public String toString() {
            return value;
        }
    }
    @RequiredArgsConstructor
    public
    enum ResponseFormat {
        url("url"),
        b64_json("b64_json");

        private final String value;

        @Override
        public String toString() {
            return value;
        }
    }

    /** A text description of the desired image(s). The maximum length is 1000 characters. */
    String prompt;

    /** The number of images to generate. Must be between 1 and 10. */
    @Builder.Default
    int number = 1;

    /** The size of the generated images. Must be one of 256x256, 512x512, or 1024x1024. */
    @Builder.Default
    Size size = Size.large;

    /** The format in which the generated images are returned. Must be one of url or b64_json. */
    @Builder.Default
    ResponseFormat responseFormat = ResponseFormat.url;

    /** A unique identifier representing your end-user, which can help OpenAI to monitor and detect abuse. Learn more. */
    @Builder.Default
    String user = null;

    @Override
    public String getEndpoint() {
        return "images/generations";
    }

    @Override
    public ImagesResponse parseResponse(String rawResponse) {
        return new ImagesResponse(new JSONObject(rawResponse));
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.POST;
    }

    @Override
    public void modifyHeaders(HttpHeaders headers) {
        headers.set(HttpHeaderNames.CONTENT_TYPE, "application/json");
    }

    public JSONObject getBody() {
        JSONObject body = new JSONObject();
        body.put("prompt", prompt);
        body.put("n", number);
        body.put("size", size.toString());
        body.put("response_format", responseFormat.toString());

        return body;
    }
}
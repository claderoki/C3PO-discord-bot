package com.c3po.core.openai.responses;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;

@RequiredArgsConstructor
@Getter
public class B64Json {
    private final String value;

    public InputStream asStream() {
        byte[] decodedBytes = Base64.getDecoder().decode(value);
        return new ByteArrayInputStream(decodedBytes);
    }
}

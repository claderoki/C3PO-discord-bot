package com.c3po.core.resource;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Stream;

public class Resource {
    private final InputStream stream;
    public Resource(String name) {
        stream = getClass().getClassLoader().getResourceAsStream(name);
        assert stream != null;
    }

    public Stream<String> getLines() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        return reader.lines();
    }
}

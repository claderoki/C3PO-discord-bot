package com.c3po.connection.query;

import com.c3po.database.Parameter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class Query {
    private final String query;
    private final List<Parameter<?>> parameters;
}

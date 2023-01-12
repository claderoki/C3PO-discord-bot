package com.c3po.connection.query;

import com.c3po.database.Parameter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class QueryBuilder {
    private final String base;
    private final ArrayList<Parameter> parameters = new ArrayList<>();
    private final HashSet<String> wheres = new HashSet<>();

    public QueryBuilder(String base) {
        this(base, List.of());
    }

    public QueryBuilder(String base, List<Parameter> parameters) {
        this.base = base;
        this.parameters.addAll(parameters);
    }

    public void addWhere(String where, List<Parameter> parameters) {
        wheres.add(where);
        this.parameters.addAll(parameters);
    }

    public void addWhere(String where, Parameter... parameters) {
        addWhere(where, List.of(parameters));
    }

    public void addWhere(String where) {
        addWhere(where, List.of());
    }

    public Query build() {


        return null;
    }
}

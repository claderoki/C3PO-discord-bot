package com.c3po.helper;

import com.c3po.database.Parameter;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Getter
@AllArgsConstructor
public class PlaceholderList {
    List<Parameter<?>> parameters;
    String questionMarks;

    public void addParameter(Parameter<?> parameter) {
        parameters.add(parameter);
    }

    public void addBefore(Parameter<?> parameter) {
        parameters.add(0, parameter);
    }

    public static PlaceholderList of(Object[] values) {
        List<Parameter<?>> parameters = new ArrayList<>();
        StringBuilder questionMarks = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            Object value = values[i];
            questionMarks.append("?");
            if (i != values.length-1) {
                questionMarks.append(",");
            }
            parameters.add(Parameter.from(value));
        }

        return new PlaceholderList(parameters, questionMarks.toString());
    }

    public static PlaceholderList of(Collection<Object> values) {
        return of(values.toArray());
    }

    public static PlaceholderList of(Set<Object> values) {
        return of(values.toArray());
    }

}

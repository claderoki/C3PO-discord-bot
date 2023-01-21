package com.c3po.database;

import lombok.Setter;
import lombok.experimental.Accessors;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

@Setter
@Accessors(chain = true)
public abstract class Parameter<T> {
    protected final boolean nullable;
    protected final T value;

    public Parameter(T value) {
        this(value, false);
    }

    public Parameter(T value, boolean nullable) {
        if (!nullable && value == null) {
            throw new RuntimeException("Value cannot be null.");
        }
        this.value = value;
        this.nullable = nullable;
    }

    protected abstract void bind(PreparedStatement preparedStatement, int index) throws SQLException;

    public final void bindParam(PreparedStatement preparedStatement, int index) throws SQLException {
        if (this.value == null) {
            preparedStatement.setNull(index, Types.VARCHAR);
        } else {
            bind(preparedStatement, index);
        }
    }

    public static Parameter<?> from(Object value) {
        if (value == null) {
            return new NullParameter();
        }
        if (value instanceof Long v) {
            return new LongParameter(v);
        }
        if (value instanceof Integer v) {
            return new IntParameter(v);
        }
        return new StringParameter(value.toString());
    }
}

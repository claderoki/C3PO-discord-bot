package com.c3po.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BoolParameter extends Parameter<Boolean> {
    public BoolParameter(Boolean value) {
        super(value);
    }

    public BoolParameter(Boolean value, boolean nullable) {
        super(value, nullable);
    }

    @Override
    public void bind(PreparedStatement preparedStatement, int index) throws SQLException {
        preparedStatement.setInt(index, value ? 1 : 0);
    }
}

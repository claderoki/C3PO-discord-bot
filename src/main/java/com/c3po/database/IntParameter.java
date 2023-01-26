package com.c3po.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class IntParameter extends Parameter<Integer> {
    public IntParameter(Integer value) {
        super(value);
    }

    public IntParameter(Integer value, boolean nullable) {
        super(value, nullable);
    }

    @Override
    public void bind(PreparedStatement preparedStatement, int index) throws SQLException {
        preparedStatement.setInt(index, value);
    }

}

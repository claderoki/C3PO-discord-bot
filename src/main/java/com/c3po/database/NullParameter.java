package com.c3po.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class NullParameter extends Parameter<Void> {

    public NullParameter() {
        super(null);
    }

    @Override
    public void bind(PreparedStatement preparedStatement, int index) throws SQLException {
        preparedStatement.setNull(index, Types.VARCHAR);
    }
}

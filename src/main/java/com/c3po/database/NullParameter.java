package com.c3po.database;

import lombok.AllArgsConstructor;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
@AllArgsConstructor
public class NullParameter extends Parameter {

    @Override
    public void bind(PreparedStatement preparedStatement, int index) throws SQLException {
        preparedStatement.setNull(index, Types.VARCHAR);
    }

    @Override
    public void bind(StringBuilder query) {
        query.append("NULL");
    }
}

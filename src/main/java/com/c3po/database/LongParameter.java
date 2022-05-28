package com.c3po.database;

import discord4j.common.util.Snowflake;
import lombok.AllArgsConstructor;

import java.sql.PreparedStatement;
import java.sql.SQLException;
@AllArgsConstructor
public class LongParameter extends Parameter {
    final long value;

    public LongParameter(Snowflake value) {
        this.value = value.asLong();
    }

    @Override
    public void bind(PreparedStatement preparedStatement, int index) throws SQLException {
        preparedStatement.setLong(index, value);
    }

    @Override
    public void bind(StringBuilder query) {
        query.append(value);
    }
}

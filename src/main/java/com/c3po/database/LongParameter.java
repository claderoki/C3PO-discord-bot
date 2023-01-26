package com.c3po.database;

import discord4j.common.util.Snowflake;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class LongParameter extends Parameter<Long> {
    public LongParameter(Long value) {
        super(value);
    }

    public LongParameter(Long value, boolean nullable) {
        super(value, nullable);
    }

    public LongParameter(Snowflake value) {
        this(value, false);
    }

    public LongParameter(Snowflake value, boolean nullable) {
        super(value.asLong(), nullable);
    }

    @Override
    public void bind(PreparedStatement preparedStatement, int index) throws SQLException {
        preparedStatement.setLong(index, value);
    }
}

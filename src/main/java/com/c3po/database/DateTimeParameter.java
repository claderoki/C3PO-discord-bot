package com.c3po.database;

import com.c3po.helper.DateTimeHelper;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class DateTimeParameter extends Parameter<LocalDateTime> {
    public DateTimeParameter(LocalDateTime value) {
        super(value);
    }

    public DateTimeParameter(LocalDateTime value, boolean nullable) {
        super(value, nullable);
    }

    @Override
    public void bind(PreparedStatement preparedStatement, int index) throws SQLException {
        preparedStatement.setString(index, value.format(DateTimeHelper.DATETIME_FORMATTER));
    }
}

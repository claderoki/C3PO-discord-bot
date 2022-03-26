package com.c3po.database;

import com.c3po.helper.DateTimeHelper;
import lombok.AllArgsConstructor;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;

@AllArgsConstructor
public class DateTimeParameter extends Parameter {
    final LocalDateTime value;

    @Override
    public void bind(PreparedStatement preparedStatement, int index) throws SQLException {
        preparedStatement.setString(index, value.format(DateTimeHelper.DATETIME_FORMATTER));
    }

    @Override
    public void bind(StringBuilder query) {
        query.append(value);
    }
}

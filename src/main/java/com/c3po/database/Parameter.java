package com.c3po.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class Parameter {
    public abstract void bind(PreparedStatement preparedStatement, int index) throws SQLException;
    public abstract void bind(StringBuilder query);
}

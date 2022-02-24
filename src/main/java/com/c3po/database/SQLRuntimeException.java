package com.c3po.database;

import java.sql.SQLException;

public class SQLRuntimeException extends RuntimeException {
    public SQLRuntimeException(SQLException cause) {
        super(cause);
    }
}

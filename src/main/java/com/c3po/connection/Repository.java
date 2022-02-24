package com.c3po.connection;

import com.c3po.database.Parameter;
import com.c3po.database.Result;
import com.c3po.database.SQLRuntimeException;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Repository {
    final private DataSource dataSource;

    public Repository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private String[] getColumnLabels(ResultSet set) throws SQLException {
        ResultSetMetaData metadata = set.getMetaData();
        String[] columnLabels = new String[metadata.getColumnCount()];
        for (int i = 0; i < metadata.getColumnCount(); i++) {
            columnLabels[i] = metadata.getColumnLabel(i+1);
        }
        return columnLabels;
    }

    private Connection getConnection() {
        try {
            return this.dataSource.getConnection();
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    private PreparedStatement preparedStatement(Connection connection, String query, Parameter... params) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(query);
        int index = 1;
        for (Parameter parameter: params) {
            parameter.bind(statement, index++);
        }
        return statement;
    }

    protected void update(Connection connection, String query, Parameter... params) {
        try (PreparedStatement statement = preparedStatement(connection, query, params)) {
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    protected void update(String query, Parameter... params) {
        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = preparedStatement(connection, query, params)) {
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    protected List<Result> query(Connection connection, String query, Parameter... params) {
        ArrayList<Result> results = new ArrayList<>();
        try (PreparedStatement statement = preparedStatement(connection, query, params)) {
            try (ResultSet rs = statement.executeQuery()) {
                String[] columnLabels = null;
                while (rs.next()) {
                    if (columnLabels == null) {
                        columnLabels = getColumnLabels(rs);
                    }
                    results.add(new Result(rs, columnLabels));
                }
            }
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
        return results;
    }

    protected List<Result> query(String query, Parameter... params) {
        try (Connection connection = getConnection()) {
            return query(connection, query, params);
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    protected List<Result> query(String query, Collection<Parameter> params) {
        return query(query, params.toArray(new Parameter[0]));
    }

    protected Result getOne(String query, Parameter... params) {
        List<Result> results = query(query, params);
        if (results.isEmpty()) {
            return null;
        }
        return results.get(0);
    }

    protected Result getOne(String query, Collection<Parameter> params) {
        return getOne(query, params.toArray(new Parameter[0]));
    }

}

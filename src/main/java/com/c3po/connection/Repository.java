package com.c3po.connection;

import com.c3po.connection.query.Query;
import com.c3po.database.DataSourceLoader;
import com.c3po.database.Parameter;
import com.c3po.database.SQLRuntimeException;
import com.c3po.database.result.ColumnContainer;
import com.c3po.database.result.Result;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.annotation.Nullable;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class Repository {
    final private DataSource dataSource;

    public Repository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Repository() {
        this(DataSourceLoader.instance());
    }

    private ColumnContainer getColumnContainer(ResultSet set) throws SQLException {
        ResultSetMetaData metadata = set.getMetaData();
        ColumnContainer container = new ColumnContainer();
        for (int i = 0; i < metadata.getColumnCount(); i++) {
            container.put(metadata.getColumnLabel(i+1), i);
        }
        return container;
    }

    private Connection getConnection() {
        try {
            return this.dataSource.getConnection();
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    private PreparedStatement preparedStatement(Connection connection, String query, Parameter<?>... params) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(query);
        int index = 1;
        for (Parameter<?> parameter: params) {
            parameter.bindParam(statement, index++);
        }
        return statement;
    }

    protected final int execute(Connection connection, String query, Parameter<?>... params) {
        try (PreparedStatement statement = preparedStatement(connection, query, params)) {
            return statement.executeUpdate();
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    protected final int execute(String query, Parameter<?>... params) {
        try (Connection connection = getConnection()) {
            return execute(connection, query, params);
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    protected final Mono<Integer> monoExecute(String query, Parameter<?>... params) {
        try (Connection connection = getConnection()) {
            return Mono.just(execute(connection, query, params));
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }
    protected final List<Result> getMany(Connection connection, String query, Parameter<?>... params) {
        ArrayList<Result> results = new ArrayList<>();
        try (PreparedStatement statement = preparedStatement(connection, query, params)) {
            ColumnContainer columns = null;
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    if (columns == null) {
                        columns = getColumnContainer(rs);
                    }
                    results.add(new Result(rs, columns));
                }
            }
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
        return results;
    }

    protected final List<Result> getMany(String query, Parameter<?>... params) {
        try (Connection connection = getConnection()) {
            return getMany(connection, query, params);
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    protected final List<Result> getMany(String query, Collection<Parameter<?>> params) {
        return getMany(query, params.toArray(new Parameter[0]));
    }

    protected final Stream<Result> streamMany(String query, Collection<Parameter<?>> params) {
        return getMany(query, params).stream();
    }

    protected final Stream<Result> streamMany(Query query) {
        return getMany(query.getQuery(), query.getParameters()).stream();
    }

    protected final Flux<Result> fluxMany(Query query) {
        return Flux.fromIterable(getMany(query.getQuery(), query.getParameters()));
    }

    protected final Optional<Result> optOne(String query, Parameter<?>... params) {
        return Optional.ofNullable(getOne(query, params));
    }

    protected final @Nullable Result getOne(String query, Parameter<?>... params) {
        List<Result> results = getMany(query, params);
        if (results.isEmpty()) {
            return null;
        }
        return results.get(0);
    }

    protected final @Nullable Result getOne(String query, Collection<Parameter<?>> params) {
        return getOne(query, params.toArray(new Parameter[0]));
    }
}

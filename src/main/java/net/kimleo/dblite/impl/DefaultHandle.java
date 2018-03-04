package net.kimleo.dblite.impl;

import net.kimleo.dblite.Handle;
import net.kimleo.dblite.exception.DBLiteException;
import net.kimleo.dblite.functional.Extractor;
import net.kimleo.dblite.functional.Injector;
import net.kimleo.dblite.functional.Preparer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static java.lang.String.format;

public class DefaultHandle implements Handle, AutoCloseable {

    private Connection connection;

    public DefaultHandle(Connection connection) {
        this.connection = connection;
    }

    @Override
    public <T> T one(String query, Extractor<T> extractor) {
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            if (resultSet.next()) {
                return extractor.from(resultSet);
            }
            throw new DBLiteException(new NoSuchElementException(format("Cannot found an element from query: %s", query)));
        } catch (Exception ignored) {
            throw new DBLiteException(ignored);
        }
    }

    @Override
    public <T> List<T> more(String query, Extractor<T> extractor) {
        ArrayList<T> results = new ArrayList<>();
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query);) {

            while (resultSet.next()) {
                results.add(extractor.from(resultSet));
            }
        } catch (Exception ignored) {
            throw new DBLiteException(ignored);
        }
        return results;
    }

    @Override
    public <T> void update(T object, Injector<T> injector) {
        try (Statement statement = connection.createStatement()) {
            statement.execute(injector.inject(object));
        } catch (Exception ignored) {
            throw new DBLiteException(ignored);
        }
    }

    @Override
    public <T> void update(Iterable<T> iterable, Injector<T> injector) {
        try (Statement statement = connection.createStatement()) {
            for (T object : iterable) {
                statement.execute(injector.inject(object));
            }
        } catch (Exception ignored) {
            throw new DBLiteException(ignored);
        }
    }

    @Override
    public void execute(String stmt) {
        try (Statement statement = connection.createStatement()) {
            statement.execute(stmt);
        } catch (Exception e) {
            throw new DBLiteException(e);
        }
    }

    @Override
    public ResultSet query(String query) {
        try (Statement stmt = connection.createStatement()) {
            return stmt.executeQuery(query);
        } catch (Exception e) {
            throw new DBLiteException(e);
        }
    }

    @Override
    public <T> void prepared(T object, String stmt, Preparer<T> preparer) {
        try (PreparedStatement prepared = connection.prepareStatement(stmt)) {
            preparer.prepare(prepared, object);
            prepared.execute();
        } catch (Exception e) {
            throw new DBLiteException(e);
        }
    }

    @Override
    public <T> void prepared(Iterable<T> ts, String stmt, Preparer<T> preparer) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(stmt)) {
            for (T t : ts) {
                preparedStatement.clearParameters();
                preparer.prepare(preparedStatement, t);
                preparedStatement.execute();
            }
        } catch (Exception e) {
            throw new DBLiteException(e);
        }
    }

    @Override
    public <P, T> T prepOne(String query, P param, Preparer<P> preparer, Extractor<T> extractor) {
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            preparer.prepare(ps, param);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return extractor.from(rs);
            }
        } catch (Exception e) {
            throw new DBLiteException(e);
        }
    }

    @Override
    public <P, T> List<T> prepMore(String query, Iterable<P> params, Preparer<P> preparer, Extractor<T> extractor) {
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            List<T> results = new ArrayList<>();
            for (P param : params) {
                ps.clearParameters();
                preparer.prepare(ps, param);
                try (ResultSet resultSet = ps.executeQuery()) {
                    while (resultSet.next()) {
                        results.add(extractor.from(resultSet));
                    }
                }
            }
            return results;
        } catch (Exception e) {
            throw new DBLiteException(e);
        }
    }

    @Override
    public void close() {
        try {
            connection.close();
        } catch (SQLException ignored) {
            throw new DBLiteException(ignored);
        }
    }
}

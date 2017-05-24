package net.kimleo.dblite.impl;

import net.kimleo.dblite.Handle;
import net.kimleo.dblite.exception.DBLiteException;
import net.kimleo.dblite.functional.Extractor;
import net.kimleo.dblite.functional.Injector;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(query);
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
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(query);
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
        } catch (SQLException ignored) {
            throw new DBLiteException(ignored);
        }
    }

    @Override
    public <T> void update(Iterable<T> iterable, Injector<T> injector) {
        try (Statement statement = connection.createStatement()) {
            for (T object : iterable) {
                statement.execute(injector.inject(object));
            }
        } catch (SQLException ignored) {
            throw new DBLiteException(ignored);
        }
    }

    @Override
    public void execute(String stmt) {
        try (Statement statement = connection.createStatement()) {
            statement.execute(stmt);
        } catch (SQLException e) {
            throw new DBLiteException(e);
        }
    }

    @Override
    public ResultSet query(String query) {
        try {
            return connection.createStatement().executeQuery(query);
        } catch (SQLException e) {
            throw new DBLiteException(e);
        }
    }

    @Override
    public void close() throws IOException {
        try {
            connection.close();
        } catch (SQLException ignored) {
            throw new DBLiteException(ignored);
        }
    }
}

package net.kimleo.dblite;

import net.kimleo.dblite.exception.DBLiteException;
import net.kimleo.dblite.impl.ConnectionSupplierDB;
import net.kimleo.dblite.impl.DataSourceDB;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface DB {
    Handle handle();

    default void withHandle(Consumer<Handle> consumer) {
        try (Handle handle = handle()) {
            consumer.accept(handle);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static DB connect(DataSource ds) {
        return new DataSourceDB(ds);
    }

    static DB connect(Supplier<Connection> supplier) {
        return new ConnectionSupplierDB(supplier);
    }

    static DB connect(String url, String username, String password) {
        return connect(() -> {
            try {
                return DriverManager.getConnection(url, username, password);
            } catch (SQLException e) {
                throw new DBLiteException(e);
            }
        });
    }
}

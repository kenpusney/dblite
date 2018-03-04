package net.kimleo.dblite.functional;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface Preparer<T> {
    void prepare(PreparedStatement stmt, T object) throws SQLException;
}

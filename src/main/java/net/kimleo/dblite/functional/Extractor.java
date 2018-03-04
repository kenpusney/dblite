package net.kimleo.dblite.functional;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface Extractor<T> {
    T from(ResultSet rs) throws SQLException;
}

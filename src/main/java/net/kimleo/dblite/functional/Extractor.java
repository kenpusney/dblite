package net.kimleo.dblite.functional;

import java.sql.ResultSet;

public interface Extractor<T> {
    T from(ResultSet rs) throws Exception;
}

package net.kimleo.dblite;

import net.kimleo.dblite.functional.Extractor;
import net.kimleo.dblite.functional.Injector;

import java.io.Closeable;
import java.sql.ResultSet;
import java.util.List;

public interface Handle extends Closeable {
    <T> T one(String query, Extractor<T> extractor);
    <T> List<T> more(String query, Extractor<T> extractor);
    <T> void update(T object, Injector<T> translator);
    <T> void update(Iterable<T> ts, Injector<T> translator);

    void execute(String statement);
    ResultSet query(String query);
}

package net.kimleo.dblite;

import net.kimleo.dblite.exception.DBLiteException;
import net.kimleo.dblite.functional.Extractor;
import net.kimleo.dblite.functional.Injector;
import net.kimleo.dblite.functional.Preparer;

import java.io.Closeable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public interface Handle extends Closeable {
    <T> T one(String query, Extractor<T> extractor);

    <T> List<T> more(String query, Extractor<T> extractor);

    <T> void update(T object, Injector<T> translator);

    <T> void update(Iterable<T> ts, Injector<T> translator);

    void execute(String statement);

    @Deprecated
    ResultSet query(String query);

    <T> void prepared(T object, String stmt, Preparer<T> preparer);

    <T> void prepared(Iterable<T> ts, String stmt, Preparer<T> preparer);

    <P, T> T prepOne(String query, P param, Preparer<P> preparer, Extractor<T> extractor);

    <P, T> List<T> prepMore(String query, Iterable<P> params, Preparer<P> preparer, Extractor<T> extractor);

}

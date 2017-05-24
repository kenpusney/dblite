package net.kimleo.dblite.impl;

import net.kimleo.dblite.DB;
import net.kimleo.dblite.Handle;
import net.kimleo.dblite.exception.DBLiteException;

import javax.sql.DataSource;
import java.sql.SQLException;

public class DataSourceDB implements DB {
    private final DataSource dataSource;

    public DataSourceDB(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Handle handle() {
        try {
            return new DefaultHandle(dataSource.getConnection());
        } catch (SQLException e) {
            throw new DBLiteException(e);
        }
    }
}

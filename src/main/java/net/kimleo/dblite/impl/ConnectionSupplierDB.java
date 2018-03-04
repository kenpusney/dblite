package net.kimleo.dblite.impl;

import net.kimleo.dblite.DB;
import net.kimleo.dblite.Handle;
import net.kimleo.dblite.exception.DBLiteException;
import net.kimleo.dblite.functional.Connector;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Supplier;

public class ConnectionSupplierDB implements DB{
    private final Connector supplier;

    public ConnectionSupplierDB(Connector supplier) {
        this.supplier = supplier;
    }


    @Override
    public Handle handle() {
        try {
            return new DefaultHandle(supplier.connect());
        } catch (SQLException e) {
            throw new DBLiteException(e);
        }
    }
}

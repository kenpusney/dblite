package net.kimleo.dblite.impl;

import net.kimleo.dblite.DB;
import net.kimleo.dblite.Handle;

import java.sql.Connection;
import java.util.function.Supplier;

public class ConnectionSupplierDB implements DB{
    private final Supplier<Connection> supplier;

    public ConnectionSupplierDB(Supplier<Connection> supplier) {
        this.supplier = supplier;
    }


    @Override
    public Handle handle() {
        return new DefaultHandle(supplier.get());
    }
}

package net.kimleo.dblite.exception;

public class DBLiteException extends RuntimeException {
    public DBLiteException(Throwable cause) {
        super(cause);
        cause.printStackTrace();
    }
}

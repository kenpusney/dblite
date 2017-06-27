package net.kimleo.dblite.repl;

import net.kimleo.dblite.DB;
import net.kimleo.dblite.exception.DBLiteException;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Repl {

    private final DB db;
    private final PrintStream out;
    private final boolean redirected;

    public Repl(DB db) {
        this.db = db;
        out = System.out;
        redirected = (System.console() == null);
    }

    public void run(BufferedReader reader) {
        for (; ; ) {
            try {
                prompt();
                String expr = read(reader);
                if (expr.trim().isEmpty()) {
                    continue;
                }
                eval(expr);
            } catch (DBLiteException e) {
                out.printf("Error found when execute: %s%n", e.getCause().toString());
            } catch (EOFException e) {
                if (!redirected)
                    out.printf("%nBye bye%n");
                break;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void prompt() {
        if (!redirected) {
            out.print("db>> ");
        }
    }

    private void subPrompt() {
        if (!redirected) {
            out.print("  => ");
        }
    }

    private void eval(String expr) {
        db.withHandle(handle -> {
            handle.more(expr, rs -> {
                ResultSetMetaData md = rs.getMetaData();
                header(md);
                rows(rs, md);
                return rs;
            });
        });
    }

    private void rows(ResultSet rs, ResultSetMetaData md) throws SQLException {
        do {
            out.print("||");
            for (int i = 1; i <= md.getColumnCount(); i++) {
                out.print(leftpad(rs.getString(i), md.getColumnDisplaySize(i)));
                out.print("|");
            }
            out.println("|");
        } while (rs.next());
    }

    private void header(ResultSetMetaData md) throws SQLException {
        out.print("||");
        for (int i = 1; i <= md.getColumnCount(); i++) {
            String name = md.getColumnName(i) + "(" + md.getColumnTypeName(i) + ")";
            out.print(leftpad(name, md.getColumnDisplaySize(i)));
            out.print("|");
        }
        out.println("|");
    }

    private String leftpad(String name, int columnDisplaySize) {
        if (columnDisplaySize < name.length()) {
            return name.substring(0, columnDisplaySize);
        }

        String spaces = IntStream
                .range(0, columnDisplaySize - name.length()).mapToObj(it -> " ")
                .collect(Collectors.joining(""));
        return spaces + name;
    }


    private String read(BufferedReader br) throws IOException {
        StringBuilder sb = new StringBuilder();

        String segment = br.readLine();
        if (segment == null) {
            throw new EOFException();
        }
        while (!segment.trim().endsWith(";")) {
            sb.append(segment).append(" ");
            subPrompt();
            segment = br.readLine();
            if (segment == null) {
                if (!redirected) out.println();
                break;
            }
        }
        sb.append(segment != null ? segment : "");

        return sb.toString();
    }
}

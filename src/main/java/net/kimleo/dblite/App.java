package net.kimleo.dblite;

import net.kimleo.dblite.repl.Repl;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class App {
    public static void main(String[] args) throws ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        new Repl(DB.connect("jdbc:postgresql://localhost/postgres", "postgres", ""))
                .run(new BufferedReader(new InputStreamReader(System.in)));
    }
}

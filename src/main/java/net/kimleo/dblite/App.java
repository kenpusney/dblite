package net.kimleo.dblite;

import net.kimleo.dblite.repl.CommandOptionItem;
import net.kimleo.dblite.repl.Repl;
import net.kimleo.dblite.repl.ReplConfig;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static net.kimleo.dblite.repl.CommandOptionItem.cmd;

public class App {

    public static final ArrayList<CommandOptionItem> cmds = new ArrayList<CommandOptionItem>() {{
        add(cmd("-L", "url", "DB_URL"));
        add(cmd("-U", "username", "DB_USER"));
        add(cmd("-P", "password", "DB_PASS"));
    }};

    public static void main(String[] args) throws ClassNotFoundException {
        ReplConfig config = parseArgs(args);
        new Repl(DB.connect(config.url(), config.user(), config.password()))
                .run(new BufferedReader(new InputStreamReader(System.in)));
    }

    private static ReplConfig parseArgs(String[] args) {

        HashMap<String, String> properties = new HashMap<>();

        Map<String, Consumer<Integer>> handlers = new HashMap<String, Consumer<Integer>>() {{
            for (CommandOptionItem cmd : cmds) {
                put(cmd.option(), (index) -> {
                    properties.put(cmd.name(), args[index + 1]);
                    properties.putIfAbsent(cmd.name(), System.getenv(cmd.env()));
                });
            }
        }};

        for (int i = 0; i < args.length; i++) {
            String current = args[i];
            if (handlers.containsKey(current)) {
                handlers.get(current).accept(i);
                i ++;
            }
        }

        return new ReplConfig(
                properties.get("url"),
                properties.get("username"),
                properties.getOrDefault("password", ""));
    }
}

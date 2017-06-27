package net.kimleo.dblite.repl;

public class CommandOptionItem {
    private final String option;
    private final String name;
    private final String env;

    private CommandOptionItem(String option, String name, String env) {
        this.option = option;
        this.name = name;
        this.env = env;
    }

    public static CommandOptionItem cmd(String option, String name, String env) {
        return new CommandOptionItem(option, name, env);
    }

    public String option() {
        return option;
    }

    public String name() {
        return name;
    }

    public String env() {
        return env;
    }
}

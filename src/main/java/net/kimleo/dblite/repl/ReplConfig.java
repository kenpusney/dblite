package net.kimleo.dblite.repl;

public class ReplConfig {
    final String url;
    final String username;
    final String password;

    public ReplConfig(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public String url() {
        return url;
    }

    public String user() {
        return username;
    }

    public String password() {
        return password;
    }
}

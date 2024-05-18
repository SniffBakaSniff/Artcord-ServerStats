package xyz.subaka.Utils;

import org.bukkit.Server;

public class ServerTPS {

    private final Server server;

    public ServerTPS(Server server) {
        this.server = server;
    }

    public double getTPS() {
        return server.getTPS()[0];
    }
}

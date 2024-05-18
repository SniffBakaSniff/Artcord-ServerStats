package xyz.subaka.Utils;

import org.bukkit.Server;

public class PlayerCount {

    private final Server server;

    public PlayerCount(Server server) {
        this.server = server;
    }

    public int getPlayerCount() {
        return server.getOnlinePlayers().size();
    }
}

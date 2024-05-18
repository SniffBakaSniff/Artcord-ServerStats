package xyz.subaka.Utils;

import org.bukkit.Server;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class PlayerList {

    private final Server server;

    public PlayerList(Server server) {
        this.server = server;
    }

    public List<String> getPlayerList() {
        return server.getOnlinePlayers().stream()
                .map(Player::getName)
                .collect(Collectors.toList());
    }
}

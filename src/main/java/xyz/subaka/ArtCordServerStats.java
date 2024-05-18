package xyz.subaka;

import net.dv8tion.jda.api.JDA;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.subaka.DiscordBot.Bot;
import xyz.subaka.DiscordBot.CommandLogging;
import xyz.subaka.DiscordBot.ServerStartStopmsg;
import xyz.subaka.Utils.PlayerCount;
import xyz.subaka.Utils.PlayerList;
import xyz.subaka.Utils.ServerTPS;

public class ArtCordServerStats extends JavaPlugin {

    private static PlayerCount playerCount;
    private static ServerTPS serverTPS;
    private static PlayerList playerList;
    private static String botToken;
    public static String loggingChannel;
    private Bot bot;
    private ServerStartStopmsg serverStartStopmsg;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadConfigValues();
        playerCount = new PlayerCount(getServer());
        serverTPS = new ServerTPS(getServer());
        playerList = new PlayerList(getServer());

        bot = new Bot(botToken);
        bot.start();

        bot.getJDAFuture().thenAccept(jda -> {
            serverStartStopmsg = new ServerStartStopmsg(jda , loggingChannel, getServer());
            serverStartStopmsg.sendServerStartMessage();

            getServer().getPluginManager().registerEvents(new CommandLogging(jda, loggingChannel, this), this);
        });
    }

    @Override
    public void onDisable() {
        if (serverStartStopmsg != null) {
            serverStartStopmsg.sendServerStopMessage();
        }
    }


    private void loadConfigValues() {
        FileConfiguration config = getConfig();
        botToken = config.getString("BOT_TOKEN");
        loggingChannel = config.getString("Command_Logging_Channel");
    }

    public static PlayerCount getPlayerCountProvider() {
        return playerCount;
    }

    public static ServerTPS getTpsProvider() {
        return serverTPS;
    }

    public static PlayerList getPlayerListProvider() {
        return playerList;
    }
}

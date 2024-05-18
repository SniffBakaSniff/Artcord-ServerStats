package xyz.subaka.DiscordBot;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.JDA;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;

public class ServerStartStopmsg {

    private final JDA jda;
    private final String loggingChannel;
    private final Server server;

    public ServerStartStopmsg(JDA jda, String loggingChannel, Server server) {
        this.jda = jda;
        this.loggingChannel = loggingChannel;
        this.server = server;
    }

    public void sendServerStartMessage() {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String serverIcon = "\uD83C\uDF93";


        int maxPlayers = server.getMaxPlayers();
        String motd = server.getMotd();
        int pluginCount = server.getPluginManager().getPlugins().length;

        StringBuilder pluginInfo = new StringBuilder();
        for (Plugin plugin : server.getPluginManager().getPlugins()) {
            String pluginName = plugin.getName();
            String pluginVersion = plugin.getDescription().getVersion();
            pluginInfo.append(pluginName).append(" v").append(pluginVersion).append("\n");
        }

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle(serverIcon + "Server Started")
                .setColor(Color.GREEN)
                .addField("Timestamp", timestamp, true)
                .addField("Max Player Count", String.valueOf(maxPlayers), true)
                .addField("Server MOTD", motd, false)
                .addField("Number of Plugins", String.valueOf(pluginCount), false)
                .addField("Plugins", pluginInfo.toString(), false);

        MessageEmbed messageEmbed = embedBuilder.build();

        jda.getTextChannelById(loggingChannel)
                .sendMessage("")
                .setEmbeds(messageEmbed)
                .queue();
    }

    public void sendServerStopMessage() {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String serverIcon = "\uD83C\uDF93";

        int maxPlayers = server.getMaxPlayers();
        String motd = server.getMotd();
        int pluginCount = server.getPluginManager().getPlugins().length;

        StringBuilder pluginInfo = new StringBuilder();
        for (Plugin plugin : server.getPluginManager().getPlugins()) {
            String pluginName = plugin.getName();
            String pluginVersion = plugin.getDescription().getVersion();
            pluginInfo.append(pluginName).append(" v").append(pluginVersion).append("\n");
        }

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle(serverIcon + " Server Stopped")
                .setColor(Color.RED)
                .addField("Timestamp", timestamp, true)
                .addField("Max Player Count", String.valueOf(maxPlayers), true)
                .addField("Server MOTD", motd, false)
                .addField("Number of Plugins", String.valueOf(pluginCount), false)
                .addField("Plugins", pluginInfo.toString(), false); // Adding the plugin names and versions field

        MessageEmbed messageEmbed = embedBuilder.build();

        jda.getTextChannelById(loggingChannel)
                .sendMessage("")
                .setEmbeds(messageEmbed)
                .queue();
    }
}

package xyz.subaka.DiscordBot;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;

public class CommandLogging implements Listener {

    private final JDA jda;
    private final String loggingChannel;
    private final JavaPlugin plugin;

    public CommandLogging(JDA jda, String loggingChannel, JavaPlugin plugin) {
        this.jda = jda;
        this.loggingChannel = loggingChannel;
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerCommand(@Nonnull PlayerCommandPreprocessEvent event) {
        String playerName = event.getPlayer().getName();
        String command = event.getMessage();
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String baseCommand = command.split(" ")[0].substring(1);

        plugin.getLogger().log(Level.INFO, "Base command: " + baseCommand);

        @Nullable Command cmd = getCommand(baseCommand);
        if (cmd != null) {
            if (cmd instanceof PluginCommand) {
                PluginCommand pluginCommand = (PluginCommand) cmd;
                String pluginName = pluginCommand.getPlugin().getName();
                sendEmbeddedMessage(playerName, command, timestamp, pluginName);
            } else {
                sendEmbeddedMessage(playerName, command, timestamp, "Server");
            }
        } else {
            plugin.getLogger().log(Level.INFO, "Command not found or no permission: " + baseCommand);
            sendFailureEmbed(playerName, command, timestamp, "Invalid Command or No Permission", "Unknown");
        }
    }

    private Command getCommand(String name) {
        try {
            Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            CommandMap commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());
            return commandMap.getCommand(name);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to access CommandMap", e);
            return null;
        }
    }

    private void sendFailureEmbed(String playerName, String command, String timestamp, String failureReason, String pluginName) {
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle("Command Execution Failed")
                .setColor(Color.RED)
                .addField("Player Name", playerName, true)
                .addField("Command", command, true)
                .addField("Timestamp", timestamp, true)
                .addField("Failure Reason", failureReason, false)
                .addField("Command Origin", pluginName, false);

        MessageEmbed messageEmbed = embedBuilder.build();

        jda.getTextChannelById(loggingChannel)
                .sendMessage("")
                .setEmbeds(messageEmbed)
                .queue();
    }

    private void sendEmbeddedMessage(String playerName, String command, String timestamp, String pluginName) {
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle("Minecraft Command Logged")
                .setColor(Color.GREEN)
                .addField("Player Name", playerName, true)
                .addField("Command", command, true)
                .addField("Timestamp", timestamp, true)
                .addField("Command Origin", pluginName, true);

        MessageEmbed messageEmbed = embedBuilder.build();

        jda.getTextChannelById(loggingChannel)
                .sendMessage("")
                .setEmbeds(messageEmbed)
                .queue();
    }
}

package xyz.subaka.DiscordBot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import xyz.subaka.ArtCordServerStats;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

public class Bot extends ListenerAdapter {

    private final JDABuilder builder;
    private CompletableFuture<JDA> jdaFuture;

    public Bot(String botToken) {
        this.builder = JDABuilder.createDefault(botToken);
        builder.addEventListeners(this);

        jdaFuture = new CompletableFuture<>();
    }

    @Override
    public void onReady(@Nonnull ReadyEvent event) {
        super.onReady(event);
        jdaFuture.complete(event.getJDA());
    }

    public void start() {
        builder.build();
    }

    public CompletableFuture<JDA> getJDAFuture() {
        return jdaFuture;
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        getJDAFuture().thenAccept(jda -> {
            switch (event.getName()) {
                case "playercount":
                    sendPlayerCount(event);
                    break;

                case "tps":
                    sendTPS(event);
                    break;

                case "playerlist":
                    sendPlayerList(event);
                    break;

                default:
                    break;
            }
        });
    }

    private void sendPlayerCount(SlashCommandInteractionEvent event) {
        int playerCount = ArtCordServerStats.getPlayerCountProvider().getPlayerCount();
        event.reply("Player count: " + playerCount).setEphemeral(true).queue();
    }

    private void sendTPS(SlashCommandInteractionEvent event) {
        double tps = ArtCordServerStats.getTpsProvider().getTPS();
        event.reply("Server TPS: " + tps).setEphemeral(true).queue();
    }

    private void sendPlayerList(SlashCommandInteractionEvent event) {
        String playerList = String.join(", ", ArtCordServerStats.getPlayerListProvider().getPlayerList());
        event.reply("Online players: " + playerList).setEphemeral(true).queue();
    }
}

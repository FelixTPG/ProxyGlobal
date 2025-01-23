package dev.xilef.proxyGlobal;

import dev.xilef.proxyGlobal.utils.Config;
import dev.xilef.proxyGlobal.utils.UpdateChecker;
import lombok.Getter;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.logging.Logger;

@Getter
public class EventManager implements Listener {

    private final Logger logger = Main.getInstance().getLogger();

    @EventHandler
    public void onChat(ChatEvent event) {
        if (Config.isEnabled("player-message")) return;

        // get a ProxyPlayer from Connection
        final ProxiedPlayer player = (ProxiedPlayer) event.getSender();
        if (player == null) return;

        String serverName = player.getServer().getInfo().getName();
        String message = event.getMessage();

        String format = Config.getMessage("player-message")
                .replace("{player}", player.getName())
                .replace("{message}", message)
                .replace("{server}", serverName);

        // Send the message to all servers
        Main.getInstance().getProxy().getServers().forEach((name, server) -> {
            if (!name.equals(serverName)) {
                server.getPlayers().forEach(players -> {
                    players.sendMessage(new TextComponent(format));
                });
            }
        });
    }

    @EventHandler
    public void onServerSwitch(ServerSwitchEvent event) {
        if (Config.isEnabled("server-switch")) return;

        final ProxiedPlayer player = event.getPlayer();
        String serverName = player.getServer().getInfo().getName();

        String format = Config.getMessage("server-switch")
                .replace("{player}", player.getName())
                .replace("{from}", event.getFrom().getName())
                .replace("{to}", serverName);

        Main.getInstance().getProxy().getServers().forEach((name, server) -> {
            server.getPlayers().forEach(players -> {
                players.sendMessage(new TextComponent(format));
            });
        });
    }

    @EventHandler
    public void onNetworkJoin(PostLoginEvent event) {
        if (Config.isEnabled("network-join")) return;

        final ProxiedPlayer player = event.getPlayer();
        String serverName = player.getServer().getInfo().getName();

        String format = Config.getMessage("network-join")
                .replace("{player}", player.getName())
                .replace("{server}", serverName);

        Main.getInstance().getProxy().getServers().forEach((name, server) -> {
            server.getPlayers().forEach(players -> {
                players.sendMessage(new TextComponent(format));
            });
        });
    }

    @EventHandler
    public void onNetworkLeave(PlayerDisconnectEvent event) {
        if (Config.isEnabled("network-leave")) return;

        final ProxiedPlayer player = event.getPlayer();
        String serverName = player.getServer().getInfo().getName();

        String format = Config.getMessage("network-leave")
                .replace("{player}", player.getName())
                .replace("{server}", serverName);

        Main.getInstance().getProxy().getServers().forEach((name, server) -> {
            server.getPlayers().forEach(players -> {
                players.sendMessage(new TextComponent(format));
            });
        });
    }

    @EventHandler
    public void sendUpdateMessage(PostLoginEvent event) {
        if (!Config.getConfig().getBoolean("updateNotify", false)) return;

        final ProxiedPlayer player = event.getPlayer();
        if (!player.hasPermission("proxyGlobal.updateNotify")) return;

        boolean isUpdateAvailable = UpdateChecker.isUpdateAvailable();
        if (!isUpdateAvailable) return;

        player.sendMessage(new TextComponent("&7There is a new update available for &b"
                + Main.getInstance().getDescription().getName() + "&7, version &2"
                + UpdateChecker.version + "&7! You are currently running version "
                + Main.getInstance().getDescription().getVersion() + " Get the newest version here: &2https://www.spigotmc.org/resources/logiclobby.115058/"));
    }

}
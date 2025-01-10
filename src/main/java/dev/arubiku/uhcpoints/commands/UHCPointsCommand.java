package dev.arubiku.uhcpoints.commands;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import dev.arubiku.uhcpoints.UHCPoints;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.Component;

public class UHCPointsCommand {
    private final UHCPoints plugin;

    public UHCPointsCommand(UHCPoints plugin) {
        this.plugin = plugin;
        registerCommands();
    }

    private void registerCommands() {
        LifecycleEventManager<Plugin> manager = plugin.getLifecycleManager();

        manager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();
            commands.register(
                    Commands.literal("uhcpoints")
                            .executes(context -> {
                                sendMessage(context.getSource().getSender(), "messages.usage");
                                return 1;
                            })
                            .then(Commands.literal("start").executes(context -> {
                                return startUHC(context.getSource().getSender());
                            }))
                            .then(Commands.literal("end").executes(context -> {
                                return endUHC(context.getSource().getSender());
                            }))
                            .then(Commands.literal("dump").executes(context -> {
                                return dumpPoints(context.getSource().getSender());
                            }))
                            .build(),
                    "UHC Points commands",
                    List.of("uhcp"));
        });
    }

    private int startUHC(CommandSender sender) {
        Bukkit.getOnlinePlayers().forEach(player -> plugin.getPointManager().addPoints(player, "game_start"));
        plugin.getPointManager().resetFirstKill();
        sendMessage(sender, "messages.uhc-started");
        return 1;
    }

    private int endUHC(CommandSender sender) {
        List<Player> alivePlayers = Bukkit.getOnlinePlayers().stream()
                .map(player -> (Player) player)
                .filter(player -> !player.hasPermission("uhcpoints.bypass"))
                .sorted((p1, p2) -> plugin.getPointManager().getPlayerPoints(p2.getName())
                        - plugin.getPointManager().getPlayerPoints(p1.getName()))
                .toList();

        if (!alivePlayers.isEmpty()) {
            plugin.getPointManager().addPoints(alivePlayers.get(0), "first_place");
        }
        List<Player> lastDeads = plugin.getPointManager().getLastTwoDead().stream().map(s -> Bukkit.getPlayer(s))
                .toList();
        if (!lastDeads.isEmpty()) {
            plugin.getPointManager().addPoints(lastDeads.get(0), "second_place");
        }
        if (lastDeads.size() > 1) {
            plugin.getPointManager().addPoints(lastDeads.get(1), "third_place");
        }

        sendMessage(sender, "messages.uhc-ended");
        return 1;
    }

    private int dumpPoints(CommandSender sender) {
        plugin.getPointManager().dumpPoints();
        sendMessage(sender, "messages.points-dumped");
        return 1;
    }

    private void sendMessage(CommandSender sender, String configPath) {
        String message = plugin.getPointManager().getConfig().getString(configPath, "Message not found");
        Component component = plugin.getPointManager().getMiniMessage().deserialize(message);
        sender.sendMessage(component);
    }
}

package dev.arubiku.uhcpoints.commands;

import java.util.List;

import dev.arubiku.uhcpoints.managers.PointManager;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.PlayerArgument;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
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
        //registerCommands();
        loadCommands();
    }

    public static Component getColoredMessage(String message) {
        return MiniMessage.miniMessage().deserialize(message);
    }

    public void loadCommands() {
        FileConfiguration config = plugin.getPointManager().getConfig();

        var commands = new CommandAPICommand[] {
                new CommandAPICommand("start")
                        .withPermission("uhcpoints.use.command.start")
                        .executes((sender, args) -> {
                    Bukkit.getOnlinePlayers().forEach(player -> plugin.getPointManager().addPoints(player, "game_start"));
                    plugin.getPointManager().resetFirstKill();

                    String message = config.getString("messages.uhc-started",
                            "<green>UHC started. All players received 10 points.");
                    sender.sendMessage(message);
                    /*if(sender instanceof Player) {
                        Player p = (Player) sender;
                    }*/
                }),
                new CommandAPICommand("end")
                        .withPermission("uhcpoints.use.command.end")
                        .executes((sender, args) -> {
                    List<Player> alivePlayers = Bukkit.getOnlinePlayers().stream()
                            .map(player -> (Player) player)
                            .filter(player -> !player.hasPermission("uhcpoints.bypass"))
                            .sorted((p1, p2) -> plugin.getPointManager().getPlayerPoints(p2.getName())
                                    - plugin.getPointManager().getPlayerPoints(p1.getName()))
                            .toList();

                    if (!alivePlayers.isEmpty()) {
                        plugin.getPointManager().addPoints(alivePlayers.get(0), "first_place");
                    }
                    if (alivePlayers.size() > 1) {
                        plugin.getPointManager().addPoints(alivePlayers.get(1), "second_place");
                    }
                    if (alivePlayers.size() > 2) {
                        plugin.getPointManager().addPoints(alivePlayers.get(2), "third_place");
                    }

                    String message = config.getString("messages.uhc-ended",
                            "<green>UHC ended. Top players received bonus points.");
                    sender.sendMessage(getColoredMessage(message));
                }),
                new CommandAPICommand("dump")
                        .withPermission("uhcpoints.use.command.start")
                        .executes((sender, args) -> {
                    plugin.getPointManager().dumpPoints();

                    String message = config.getString("messages.points-dumped",
                            "<green><green>Points dumped to points.yml");
                    sender.sendMessage(getColoredMessage(message));
                })
        };
        new CommandAPICommand("uhcpoints")
                .withAliases("uhcp")
                .withPermission("uhcpoints.use")
                .withSubcommands(commands)
                .executes((sender, args) -> {
                    if(sender instanceof Player) {
                        Player p = (Player) sender;
                        String message = config.getString("messages.usage",
                                "<yellow>Usage: /uhcpoints <start|end|dump>");
                        p.sendMessage(message);
                    }
                })
                .register();
    }

    /*private void registerCommands() {
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
        if (alivePlayers.size() > 1) {
            plugin.getPointManager().addPoints(alivePlayers.get(1), "second_place");
        }
        if (alivePlayers.size() > 2) {
            plugin.getPointManager().addPoints(alivePlayers.get(2), "third_place");
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
    }*/
}

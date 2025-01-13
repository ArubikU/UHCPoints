package dev.arubiku.uhcpoints.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import dev.arubiku.uhcpoints.UHCPoints;
import dev.arubiku.uhcpoints.listeners.UHCPointsListener;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.range.IntegerRangeProvider;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.Component;

public class UHCPointsCommand {
    private final UHCPoints plugin;

    public UHCPointsCommand(UHCPoints plugin) {
        this.plugin = plugin;
        registerCommands();
    }

    HashMap<Player, Team> tempTeams = new HashMap<>();

    public void glowColor(Player player, ChatColor color) {
        assert Bukkit.getScoreboardManager() != null;
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        Team team = scoreboard.registerNewTeam("temp-color-team-" + UUID.randomUUID());
        team.setColor(color);
        team.addEntry(player.getDisplayName());
        tempTeams.put(player, team);
        player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, Integer.MAX_VALUE, 0));
    }

    public void stopGlowing(Player player) {
        player.removePotionEffect(PotionEffectType.GLOWING);
        tempTeams.get(player).unregister();
        tempTeams.remove(player);
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
                            .then(Commands.literal("points").executes(context -> {
                                if (context.getSource().getSender() instanceof Player player) {
                                    player.sendMessage(UHCPoints.miniMessage.deserialize(plugin.getConfig().getString(
                                            "messages.current-points",
                                            "<green>Your current points are <1>.").replace("<1>",
                                                    plugin.getPointManager().getRecordPoints(player.getName())
                                                            + "")));
                                }
                                return 1;
                            }))
                            .then(Commands.literal("start").executes(context -> {
                                if (!context.getSource().getSender().hasPermission("uhcpoints.use")
                                        && !(context.getSource().getSender() instanceof ConsoleCommandSender))
                                    return 0;
                                return startUHC(context.getSource().getSender());
                            }))
                            .then(Commands.literal("glowtop").executes(context -> {
                                if (!context.getSource().getSender().hasPermission("uhcpoints.use")
                                        && !(context.getSource().getSender() instanceof ConsoleCommandSender))
                                    return 0;

                                List<Map.Entry<String, Integer>> sortedPlayers = plugin.getPointManager().playerPoints
                                        .entrySet().stream()
                                        .filter(player -> {
                                            return !UHCPointsListener.deadPlayers.contains(player.getKey());
                                        })
                                        .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                                        .collect(Collectors.toList());

                                int top = 1;
                                for (Map.Entry<String, Integer> eplayer : sortedPlayers) {

                                    Player player = Bukkit.getPlayer(eplayer.getKey());
                                    if (top <= 3) {
                                        glowColor(player, ChatColor.RED);
                                    } else {

                                        glowColor(player, ChatColor.GREEN);
                                    }
                                    top++;
                                }

                                return 1;
                            }))
                            .then(Commands.literal("reload").executes(context -> {
                                if (!context.getSource().getSender().hasPermission("uhcpoints.use")
                                        && !(context.getSource().getSender() instanceof ConsoleCommandSender))
                                    return 0;
                                plugin.reloadConfig();
                                context.getSource().getSender().sendMessage("UHCPoints reloaded");
                                return 1;
                            }))
                            .then(Commands.literal("end").executes(context -> {
                                if (!context.getSource().getSender().hasPermission("uhcpoints.use")
                                        && !(context.getSource().getSender() instanceof ConsoleCommandSender))
                                    return 0;
                                return endUHC(context.getSource().getSender());
                            }))
                            .then(Commands.literal("dump").executes(context -> {
                                if (!context.getSource().getSender().hasPermission("uhcpoints.use")
                                        && !(context.getSource().getSender() instanceof ConsoleCommandSender))
                                    return 0;
                                return dumpPoints(context.getSource().getSender());
                            }))
                            .then(Commands.literal("givepoints")
                                    .then(Commands.argument("player", ArgumentTypes.player()).then(

                                            Commands.argument("points", ArgumentTypes.integerRange())
                                                    .executes(context -> {
                                                        if (!context.getSource().getSender()
                                                                .hasPermission("uhcpoints.use")
                                                                && !(context.getSource()
                                                                        .getSender() instanceof ConsoleCommandSender))
                                                            return 0;
                                                        Player player = context
                                                                .getArgument("player",
                                                                        PlayerSelectorArgumentResolver.class)
                                                                .resolve(context.getSource()).get(0);
                                                        Integer points = context
                                                                .getArgument("points", IntegerRangeProvider.class)
                                                                .range().lowerEndpoint();
                                                        plugin.getPointManager().addPoints(
                                                                player, points);

                                                        return 1;
                                                    }))))
                            .then(Commands.literal("setPoints")
                                    .then(Commands.argument("player", ArgumentTypes.player())
                                            .then(Commands.argument("points", ArgumentTypes.integerRange())
                                                    .executes(context -> {
                                                        if (!context.getSource().getSender()
                                                                .hasPermission("uhcpoints.use")
                                                                && !(context.getSource()
                                                                        .getSender() instanceof ConsoleCommandSender))
                                                            return 0;
                                                        Player player = context
                                                                .getArgument("player",
                                                                        PlayerSelectorArgumentResolver.class)
                                                                .resolve(context.getSource()).get(0);
                                                        Integer points = context
                                                                .getArgument("points", IntegerRangeProvider.class)
                                                                .range().lowerEndpoint();
                                                        plugin.getPointManager().addPoints(
                                                                player, points);

                                                        return 1;
                                                    }))))
                            .build(),
                    "UHC Points commands",
                    List.of("uhcp"));
        });
    }

    private int startUHC(CommandSender sender) {
        Bukkit.getOnlinePlayers().forEach(player -> {

            plugin.getPointManager().addPoints(player, "game_start");
            sendMessage(player, "messages.uhc-started");
        });
        plugin.getPointManager().resetFirstKill();
        if (sender instanceof ConsoleCommandSender) {

            sendMessage(sender, "messages.uhc-started");
        }
        return 1;
    }

    private int endUHC(CommandSender sender) {
        List<Player> alivePlayers = Bukkit.getOnlinePlayers().stream()
                .map(player -> (Player) player)
                .filter(player -> !player.hasPermission("uhcpoints.bypass")
                        && !UHCPointsListener.deadPlayers.contains(player.getName()))
                .toList();

        if (!alivePlayers.isEmpty()) {
            plugin.getPointManager().addPoints(alivePlayers.getFirst(), "first_place");
            plugin.getEffectManager().executeRankEffects(alivePlayers.getFirst(), "onWin");
        }
        List<Player> lastDeads = plugin.getPointManager().getLastTwoDead().stream().map(s -> Bukkit.getPlayer(s))
                .toList();
        if (lastDeads.size() > 1) {
            plugin.getPointManager().addPoints(lastDeads.get(1), "second_place");
            plugin.getPointManager().addPoints(lastDeads.getFirst(), "third_place");
        } else if (lastDeads.size() > 1) {
            plugin.getPointManager().addPoints(lastDeads.getFirst(), "second_place");
        }

        sendMessage(sender, "messages.uhc-ended");
        return 1;
    }

    private int dumpPoints(CommandSender sender) {
        plugin.getPointManager().dumpPoints();
        plugin.getEffectManager().savePlayerRanks();
        plugin.getPointManager().saveRecord();
        sendMessage(sender, "messages.points-dumped");
        return 1;
    }

    private void sendMessage(CommandSender sender, String configPath) {
        String message = plugin.getPointManager().getConfig().getString(configPath, "Message not found");
        Component component = plugin.getPointManager().getMiniMessage().deserialize(message);
        sender.sendMessage(component);
    }
}

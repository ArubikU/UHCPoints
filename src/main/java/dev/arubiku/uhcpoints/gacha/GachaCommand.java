package dev.arubiku.uhcpoints.gacha;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.google.common.collect.Lists;
import com.mojang.brigadier.context.CommandContext;

import dev.arubiku.uhcpoints.gacha.GachaDataManager.GachaPlayerData;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;

public class GachaCommand {
    private final GachaponManager plugin;

    public GachaCommand(GachaponManager plugin) {
        this.plugin = plugin;
        if (plugin.getPlugin().getConfig().getBoolean("options.gacha", true)) {

            registerCommands();
        }
    }

    private void registerCommands() {
        LifecycleEventManager<Plugin> manager = plugin.getPlugin().getLifecycleManager();

        manager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();
            commands.register(
                    Commands.literal("gacha")
                            .then(Commands.literal("spin").executes((contex) -> {

                                if (contex.getSource().getSender() instanceof Player player) {
                                    handleSpin(player);
                                } else {

                                    contex.getSource().getSender().sendMessage(
                                            plugin.getGachaConfigManager().getMessage("messages.player-only"));
                                }
                                return 1;
                            }))
                            .then(Commands.literal("points").executes(this::executeGachaPoints))
                            .then(Commands.literal("gui").executes(this::executeCommand))
                            .then(Commands.literal("clean").executes(context -> {
                                if (context.getSource().getSender() instanceof Player player) {
                                    GachaPlayerData data = plugin.getGachaDataManager().getPlayerData(player);
                                    data.arrowTrailEffect = null;
                                    data.auraEffect = null;
                                    data.deathEffect = null;
                                    plugin.getGachaDataManager().setPlayerData(player, data);
                                    player.sendMessage(plugin.getGachaConfigManager().getMessage("effects-remove"));
                                }
                                return 1;

                            }))
                            .build(),
                    "Gacha commands",
                    Lists.newArrayList("ga"));

        });
    }

    private int executeCommand(CommandContext<CommandSourceStack> context) {
        CommandSender sender = context.getSource().getSender();
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getGachaConfigManager().getMessage("messages.player-only"));
            return 0;
        }

        Player player = (Player) sender;
        plugin.getGachaGUI().openMainMenu(player);
        return 1;
    }

    private int executeGachaPoints(CommandContext<CommandSourceStack> context) {
        CommandSender sender = context.getSource().getSender();
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getGachaConfigManager().getMessage("messages.player-only"));
            return 0;
        }

        Player player = (Player) sender;
        GachaDataManager.GachaPlayerData playerData = plugin.getGachaDataManager().getPlayerData(player);
        player.sendMessage(plugin.getGachaConfigManager().getMessage("points-balance")
                .replaceText(builder -> builder.match("%d").replacement(String.valueOf(playerData.getPoints()))));
        return 1;
    }

    public void handleSpin(Player player) {
        GachaDataManager.GachaPlayerData playerData = plugin.getGachaDataManager().getPlayerData(player);
        int cost = plugin.getGachaConfigManager().getCost();
        if (plugin.getGachaManager().cantSpin(player)) {
            player.sendMessage(
                    plugin.getGachaConfigManager().getMessage("all-unlocked", "<red>Alredy all are unlocked."));
            return;
        }
        try {

            if (playerData.getPoints() >= cost) {
                GachaEffect effect = plugin.getGachaManager().spinGacha(player);
                playerData.setPoints(playerData.getPoints() - cost);
                playerData.addUnlockedEffect(effect);
                player.sendMessage(plugin.getGachaConfigManager().getMessage("gacha-spin-success")
                        .replaceText(builder -> builder.match("%effect%").replacement(
                                plugin.getGachaConfigManager().getPrizeTranslation(effect.getId()).getName())));
            } else {
                player.sendMessage(plugin.getGachaConfigManager().getMessage("not-enough-points"));
            }
        } catch (Throwable a) {
            player.sendMessage(
                    plugin.getGachaConfigManager().getMessage("all-unlocked", "<red>Alredy all are unlocked."));
        }
    }

}

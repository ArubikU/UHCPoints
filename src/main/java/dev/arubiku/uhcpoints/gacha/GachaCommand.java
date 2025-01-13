package dev.arubiku.uhcpoints.gacha;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.google.common.collect.Lists;
import com.mojang.brigadier.context.CommandContext;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;

public class GachaCommand {
    private final GachaponManager plugin;

    public GachaCommand(GachaponManager plugin) {
        this.plugin = plugin;
        registerCommands();
    }

    private void registerCommands() {
        LifecycleEventManager<Plugin> manager = plugin.getPlugin().getLifecycleManager();

        manager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();
            commands.register(
                    Commands.literal("gacha")
                            .then(Commands.literal("spin").executes(this::executeCommand))
                            .then(Commands.literal("points").executes(this::executeGachaPoints))
                            .then(Commands.literal("gui").executes(this::executeCommand))
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
        if (playerData.getPoints() >= cost) {
            playerData.setPoints(playerData.getPoints() - cost);
            GachaEffect effect = plugin.getGachaManager().spinGacha();
            playerData.addUnlockedEffect(effect);
            player.sendMessage(plugin.getGachaConfigManager().getMessage("gacha-spin-success")
                    .replaceText(builder -> builder.match("%effect%").replacement(effect.getId())));
        } else {
            player.sendMessage(plugin.getGachaConfigManager().getMessage("not-enough-points"));
        }
    }

}

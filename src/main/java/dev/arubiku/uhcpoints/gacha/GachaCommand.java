package dev.arubiku.uhcpoints.gacha;

import static com.mojang.brigadier.arguments.StringArgumentType.word;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;

import dev.arubiku.uhcpoints.gacha.GachaDataManager.GachaPlayerData;

public class GachaCommand {
    private final GachaponManager plugin;
    private final CommandDispatcher<CommandSender> dispatcher = new CommandDispatcher<>();

    public GachaCommand(GachaponManager plugin) {
        this.plugin = plugin;
        registerCommands();
    }

    private void registerCommands() {
        dispatcher.register(LiteralArgumentBuilder.<CommandSender>literal("gacha")
                .then(LiteralArgumentBuilder.<CommandSender>literal("spin")
                        .executes(context -> executeCommand(context.getSource())))
                .then(LiteralArgumentBuilder.<CommandSender>literal("points")
                        .executes(context -> executeGachaPoints(context.getSource())))
                .then(LiteralArgumentBuilder.<CommandSender>literal("equip")
                        .then(RequiredArgumentBuilder.<CommandSender, String>argument("effect", word())
                                .executes(context -> executeGachaEquip(context.getSource(),
                                        StringArgumentType.getString(context, "effect"))))));
    }

    private int executeCommand(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getGachaConfigManager().getMessage("player-only"));
            return 0;
        }

        Player player = (Player) sender;
        plugin.getGachaGUI().openMainMenu(player);
        return 1;
    }

    private int executeGachaPoints(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getGachaConfigManager().getMessage("player-only"));
            return 0;
        }

        Player player = (Player) sender;
        GachaPlayerData playerData = plugin.getGachaDataManager().getPlayerData(player);
        player.sendMessage(plugin.getGachaConfigManager().getMessage("points-balance")
                .replaceText(builder -> builder.match("%d").replacement(String.valueOf(playerData.getPoints()))));

        return 1;
    }

    private int executeGachaEquip(CommandSender sender, String effectId) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getGachaConfigManager().getMessage("player-only"));
            return 0;
        }

        Player player = (Player) sender;
        // Implement equip logic here
        player.sendMessage(plugin.getGachaConfigManager().getMessage("equip-effect")
                .replaceText(builder -> builder.match("%s").replacement(effectId)));

        return 1;
    }

    public void handleSpin(Player player) {
        GachaPlayerData playerData = plugin.getGachaDataManager().getPlayerData(player);
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

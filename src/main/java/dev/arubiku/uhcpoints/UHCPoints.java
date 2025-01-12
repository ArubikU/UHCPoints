package dev.arubiku.uhcpoints;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import dev.arubiku.uhcpoints.commands.UHCPointsCommand;
import dev.arubiku.uhcpoints.effects.EffectManager;
import dev.arubiku.uhcpoints.listeners.ChatListener;
import dev.arubiku.uhcpoints.listeners.UHCPointsListener;
import dev.arubiku.uhcpoints.managers.PointManager;
import dev.arubiku.uhcpoints.placeholders.UHCPointsPlaceholder;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class UHCPoints extends JavaPlugin {
    private PointManager pointManager;
    private UHCPointsCommand uhcPointsCommand;
    public static MiniMessage miniMessage;
    private EffectManager effectManager;

    @Override
    public void onEnable() {
        UHCPoints.miniMessage = MiniMessage.miniMessage();
        this.pointManager = new PointManager(this);
        this.effectManager = new EffectManager(this);

        getServer().getPluginManager().registerEvents(new UHCPointsListener(this), this);
        getServer().getPluginManager().registerEvents(new ChatListener(this), this);
        this.uhcPointsCommand = new UHCPointsCommand(this);
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderApi")) {

            new UHCPointsPlaceholder(this).register();
        }

        effectManager.loadPlayerRanks();
        pointManager.loadRecord();
        getLogger().info("UHCPoints has been enabled!");
    }

    @Override
    public void onDisable() {
        effectManager.savePlayerRanks();
        pointManager.dumpPoints();
        pointManager.saveRecord();
        getLogger().info("UHCPoints has been disabled!");
    }

    public EffectManager getEffectManager() {
        return effectManager;
    }

    public PointManager getPointManager() {
        return pointManager;
    }

    public UHCPointsCommand getUhcPointsCommand() {
        return uhcPointsCommand;
    }
}

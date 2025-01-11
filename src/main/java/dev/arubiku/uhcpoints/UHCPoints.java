package dev.arubiku.uhcpoints;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import dev.arubiku.uhcpoints.commands.UHCPointsCommand;
import dev.arubiku.uhcpoints.listeners.UHCPointsListener;
import dev.arubiku.uhcpoints.managers.PointManager;
import dev.arubiku.uhcpoints.placeholders.UHCPointsPlaceholder;

public class UHCPoints extends JavaPlugin {
    private PointManager pointManager;
    private UHCPointsCommand uhcPointsCommand;

    @Override
    public void onEnable() {
        this.pointManager = new PointManager(this);

        getServer().getPluginManager().registerEvents(new UHCPointsListener(this), this);
        this.uhcPointsCommand = new UHCPointsCommand(this);
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderApi")) {

            new UHCPointsPlaceholder(this).register();
        }

        getLogger().info("UHCPoints has been enabled!");
    }

    @Override
    public void onDisable() {
        pointManager.dumpPoints();
        getLogger().info("UHCPoints has been disabled!");
    }

    public PointManager getPointManager() {
        return pointManager;
    }

    public UHCPointsCommand getUhcPointsCommand() {
        return uhcPointsCommand;
    }
}

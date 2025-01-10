package dev.arubiku.uhcpoints;

import org.bukkit.plugin.java.JavaPlugin;

import dev.arubiku.uhcpoints.commands.UHCPointsCommand;
import dev.arubiku.uhcpoints.listeners.UHCPointsListener;
import dev.arubiku.uhcpoints.managers.PointManager;
import dev.arubiku.uhcpoints.placeholders.UHCPointsPlaceholder;

public class UHCPoints extends JavaPlugin {
    private PointManager pointManager;

    @Override
    public void onEnable() {
        this.pointManager = new PointManager(this);

        getServer().getPluginManager().registerEvents(new UHCPointsListener(this), this);
        new UHCPointsCommand(this);
        new UHCPointsPlaceholder(this).register();

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
}

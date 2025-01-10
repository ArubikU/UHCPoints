package dev.arubiku.uhcpoints;

import dev.arubiku.uhcpoints.commands.UHCPointsCommand;
import dev.arubiku.uhcpoints.listeners.UHCPointsListener;
import dev.arubiku.uhcpoints.managers.PointManager;
import dev.arubiku.uhcpoints.placeholders.UHCPointsPlaceholder;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import org.bukkit.plugin.java.JavaPlugin;

public class UHCPoints extends JavaPlugin {
    private PointManager pointManager;

    @Override
    public void onLoad() {
        CommandAPI.onLoad(new CommandAPIBukkitConfig(this).silentLogs(true));
    }

    @Override
    public void onEnable() {
        CommandAPI.onEnable();
        this.pointManager = new PointManager(this);

        getServer().getPluginManager().registerEvents(new UHCPointsListener(this), this);
        new UHCPointsCommand(this);
        new UHCPointsPlaceholder(this).register();

        getLogger().info("UHCPoints has been enabled!");
    }

    @Override
    public void onDisable() {
        CommandAPI.onDisable();
        getLogger().info("UHCPoints has been disabled!");
    }

    public PointManager getPointManager() {
        return pointManager;
    }
}

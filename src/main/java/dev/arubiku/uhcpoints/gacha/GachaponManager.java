package dev.arubiku.uhcpoints.gacha;

import org.bukkit.Bukkit;

import dev.arubiku.uhcpoints.UHCPoints;

public class GachaponManager {
    private GachaConfigManager gachaConfigManager;
    private GachaDataManager gachaDataManager;
    private GachaGui gachaGUI;
    private GachaManager gachaManager;
    public GachaCommand command;
    private UHCPoints plugin;

    public void onEnable(UHCPoints plugin) {
        // Inicializar managers
        this.gachaConfigManager = new GachaConfigManager(this);
        this.gachaDataManager = new GachaDataManager(this);
        this.gachaGUI = new GachaGui(this);
        this.plugin = plugin;
        this.gachaManager = new GachaManager(this);
        this.command = new GachaCommand(this);
        // Cargar configuración
        gachaConfigManager.loadConfig();

        // Registrar comandos y eventos
        Bukkit.getServer().getPluginManager().registerEvents(new GachaListener(this), plugin);
    }

    public UHCPoints getPlugin() {
        return plugin;
    }

    public void onDisable() {
        // Guardar datos al desactivar el plugin
        gachaDataManager.saveData();
    }

    public GachaConfigManager getGachaConfigManager() {
        return gachaConfigManager;
    }

    public GachaDataManager getGachaDataManager() {
        return gachaDataManager;
    }

    public GachaGui getGachaGUI() {
        return gachaGUI;
    }

    public GachaManager getGachaManager() {
        return gachaManager;
    }
}

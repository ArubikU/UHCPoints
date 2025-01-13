package dev.arubiku.uhcpoints.gacha;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import dev.arubiku.uhcpoints.managers.PointManager;

public class GachaDataManager {
    private final GachaponManager plugin;
    private final File dataFile;
    private FileConfiguration data;
    private Map<String, GachaPlayerData> playerDataMap;

    public GachaDataManager(GachaponManager plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getPlugin().getDataFolder(), "gacha-data.yml");
        this.playerDataMap = new HashMap<>();
        loadData();
    }

    public void loadData() {
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                plugin.getPlugin().getLogger().severe("Could not create gacha-data.yml!");
            }
        }
        data = YamlConfiguration.loadConfiguration(dataFile);
        loadPlayerData();
    }

    private void loadPlayerData() {
        playerDataMap.clear();
        if (data.contains("players")) {
            for (String playerName : data.getConfigurationSection("players").getKeys(false)) {
                int points = data.getInt("players." + playerName + ".points");
                List<String> unlockedEffects = data.getStringList("players." + playerName + ".unlockedEffects");
                playerDataMap.put(playerName, new GachaPlayerData(points, unlockedEffects));
            }
        }

        PointManager.getRecordPoints().forEach((name, points) -> {
            if (!playerDataMap.containsKey(name)) {
                playerDataMap.put(name, new GachaPlayerData(points, new ArrayList<>()));
            }

        });
    }

    public void saveData() {
        for (Map.Entry<String, GachaPlayerData> entry : playerDataMap.entrySet()) {
            String playerName = entry.getKey();
            GachaPlayerData playerData = entry.getValue();
            data.set("players." + playerName + ".points", playerData.getPoints());
            data.set("players." + playerName + ".unlockedEffects", playerData.getUnlockedEffects());
        }
        try {
            data.save(dataFile);
        } catch (IOException e) {
            plugin.getPlugin().getLogger().severe("Could not save gacha-data.yml!");
        }
    }

    public GachaPlayerData getPlayerData(Player player) {
        String playerName = player.getName();
        if (!playerDataMap.containsKey(playerName)) {
            int points = PointManager.getRecordPoints().getOrDefault(playerName, 0);
            playerDataMap.put(playerName, new GachaPlayerData(points, new ArrayList<>()));
        }
        return playerDataMap.get(playerName);
    }

    public void setPlayerData(Player player, GachaPlayerData playerData) {
        playerDataMap.put(player.getName(), playerData);
    }

    public void addPoints(Player player, int amount) {
        GachaPlayerData playerData = getPlayerData(player);
        playerData.setPoints(playerData.getPoints() + amount);
    }

    public void setPoints(Player player, int amount) {
        GachaPlayerData playerData = getPlayerData(player);
        playerData.setPoints(amount);
    }

    public static class GachaPlayerData {
        private int points;
        private List<String> unlockedEffects;
        private GachaEffect arrowTrailEffect;
        private GachaEffect deathEffect;
        private GachaEffect auraEffect;

        public GachaPlayerData(int points, List<String> unlockedEffects) {
            this.points = points;
            this.unlockedEffects = unlockedEffects;
        }

        public int getPoints() {
            return points;
        }

        public void setPoints(int points) {
            this.points = points;
        }

        public List<String> getUnlockedEffects() {
            return unlockedEffects;
        }

        public void addUnlockedEffect(GachaEffect effect) {
            unlockedEffects.add(effect.getId());
        }

        public GachaEffect getArrowTrailEffect() {
            return arrowTrailEffect;
        }

        public void setArrowTrailEffect(GachaEffect arrowTrailEffect) {
            this.arrowTrailEffect = arrowTrailEffect;
        }

        public GachaEffect getDeathEffect() {
            return deathEffect;
        }

        public void setDeathEffect(GachaEffect deathEffect) {
            this.deathEffect = deathEffect;
        }

        public GachaEffect getAuraEffect() {
            return auraEffect;
        }

        public void setAuraEffect(GachaEffect auraEffect) {
            this.auraEffect = auraEffect;
        }
    }
}

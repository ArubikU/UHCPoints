package dev.arubiku.uhcpoints.managers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import dev.arubiku.uhcpoints.UHCPoints;
import dev.arubiku.uhcpoints.api.PointProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class PointManager {
    private final UHCPoints plugin;
    public final Map<String, Integer> playerPoints = new HashMap<>();
    private final Map<String, String> playerRanks = new HashMap<>();
    private final List<String> lastTwoDead = new ArrayList<>();
    private boolean firstKillDone = false;
    public final Map<String, List<String>> playerKills = new HashMap<>();
    private final List<PointProvider> pointProviders = new ArrayList<>();
    private final FileConfiguration config;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    private final File recordFile;

    public PointManager(UHCPoints plugin) {
        this.plugin = plugin;
        this.config = loadConfig();
        this.recordFile = new File(plugin.getDataFolder(), "record.yml");
        loadRecord();
    }

    private FileConfiguration loadConfig() {
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
        }
        return YamlConfiguration.loadConfiguration(configFile);
    }

    public boolean isFirstKill(Player player) {
        return !this.playerKills.containsKey(player.getName());
    }

    public void addKill(Player player, Player dead) {
        this.playerKills.computeIfAbsent(player.getName(), k -> new ArrayList<>()).add(dead.getName());
    }

    public void registerPointProvider(PointProvider provider) {
        pointProviders.add(provider);
    }

    public void addPoints(Player player, String providerId) {
        PointProvider provider = pointProviders.stream()
                .filter(p -> p.getId().equals(providerId))
                .findFirst()
                .orElse(null);

        if (provider != null && provider.shouldAwardPoints(player)) {
            int pointsToAdd = provider.getPoints(player);
            addPoints(player, pointsToAdd);
        }
    }

    public void addPoints(Player player, int pointsToAdd) {
        if (pointsToAdd == 0)
            return;

        String playerId = player.getName();
        int currentPoints = playerPoints.getOrDefault(playerId, 0);
        playerPoints.put(playerId, currentPoints + pointsToAdd);

        int recordPointsC = recordPoints.getOrDefault(playerId, 0);
        recordPoints.put(playerId, recordPointsC + pointsToAdd);
        plugin.getGachaponManager().getGachaDataManager().addPoints(player, pointsToAdd);

        String message = config.getString("messages.points-earned",
                "<green>You earned <points> points! Total: <total>");
        Component component = miniMessage.deserialize(message
                .replace("<points>", String.valueOf(pointsToAdd))
                .replace("<total>", String.valueOf(playerPoints.get(playerId))));
        player.sendMessage(component);

        plugin.getEffectManager().updatePlayerRank(player);
    }

    public void setPoints(Player player, int pointsToSet) {

        String playerId = player.getName();
        playerPoints.put(playerId, pointsToSet);
        recordPoints.put(playerId, pointsToSet);

        String message = config.getString("messages.points-set",
                "<green>Your points setted to <points> points! Total: <total>");
        Component component = miniMessage.deserialize(message
                .replace("<points>", String.valueOf(pointsToSet))
                .replace("<total>", String.valueOf(playerPoints.get(playerId))));
        player.sendMessage(component);
        plugin.getGachaponManager().getGachaDataManager().setPoints(player, pointsToSet);
        plugin.getEffectManager().updatePlayerRank(player);
    }

    public Map<String, Integer> getPoints() {
        return playerPoints;
    }

    public int getPlayerPoints(String playerId) {
        return playerPoints.getOrDefault(playerId, 0);
    }

    public int getRecordPoints(String playerId) {
        return recordPoints.getOrDefault(playerId, 0);
    }

    public int getPlayerPlace(String playerId) {
        List<Map.Entry<String, Integer>> sortedPlayers = playerPoints.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toList());

        for (int i = 0; i < sortedPlayers.size(); i++) {
            if (sortedPlayers.get(i).getKey().equals(playerId)) {
                return i + 1;
            }
        }
        return -1;
    }

    public void resetFirstKill() {
        firstKillDone = false;
    }

    public boolean isFirstKillDone() {
        return firstKillDone;
    }

    public void setFirstKillDone() {
        firstKillDone = true;
    }

    public void addLastDead(String playerName) {
        lastTwoDead.add(playerName);
        if (lastTwoDead.size() > 2) {
            lastTwoDead.remove(0);
        }
    }

    public List<String> getLastTwoDead() {
        return new ArrayList<>(lastTwoDead);
    }

    public void dumpPoints() {
        File file = new File(plugin.getDataFolder(), "points.yml");
        YamlConfiguration pointsConfig = new YamlConfiguration();

        pointsConfig.createSection("Puntajes");
        playerPoints.forEach((uuid, value) -> {
            pointsConfig.set("Puntajes." + Bukkit.getOfflinePlayer(uuid).getName(), value);
        });

        pointsConfig.createSection("Top");
        List<Map.Entry<String, Integer>> sortedPlayers = playerPoints.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(10)
                .collect(Collectors.toList());

        for (int i = 0; i < sortedPlayers.size(); i++) {
            pointsConfig.set("Top." + (i + 1), Bukkit.getOfflinePlayer(sortedPlayers.get(i).getKey()).getName());
        }

        try {
            pointsConfig.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save points to file: " + e.getMessage());
        }
    }

    public static Map<String, Integer> recordPoints = new HashMap<>();

    public static Map<String, Integer> getRecordPoints() {
        return recordPoints;
    }

    public void loadRecord() {
        if (recordFile.exists()) {
            YamlConfiguration record = YamlConfiguration.loadConfiguration(recordFile);
            for (String key : record.getConfigurationSection("Points").getKeys(false)) {
                recordPoints.put(key, record.getInt("Points." + key));
            }
            for (String key : record.getConfigurationSection("Ranks").getKeys(false)) {
                playerRanks.put(key, record.getString("Ranks." + key));
            }
        }
    }

    public void saveRecord() {
        YamlConfiguration record = new YamlConfiguration();
        if (!record.contains("Points"))
            record.createSection("Points");
        recordPoints.forEach((name, points) -> {
            record.set("Points." + name, points);
        });
        record.createSection("Ranks", playerRanks.entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey().toString(), Map.Entry::getValue)));
        try {
            record.save(recordFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save records to file: " + e.getMessage());
        }
    }

    public void setPlayerRank(String playerId, String rank) {
        playerRanks.put(playerId, rank);
    }

    public String getPlayerRank(String playerId) {
        return playerRanks.getOrDefault(playerId, "none");
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public MiniMessage getMiniMessage() {
        return miniMessage;
    }
}

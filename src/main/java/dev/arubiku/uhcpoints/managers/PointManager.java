package dev.arubiku.uhcpoints.managers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import dev.arubiku.uhcpoints.UHCPoints;
import dev.arubiku.uhcpoints.api.PointProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class PointManager {
    private final UHCPoints plugin;
    private final Map<UUID, Integer> playerPoints = new HashMap<>();
    private final Map<UUID, String> playerRanks = new HashMap<>();
    private final List<String> lastTwoDead = new ArrayList<>();
    private boolean firstKillDone = false;
    public final Map<UUID, List<String>> playerKills = new HashMap<>();
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
        return !this.playerKills.containsKey(player.getUniqueId());
    }

    public void addKill(Player player, Player dead) {
        this.playerKills.computeIfAbsent(player.getUniqueId(), k -> new ArrayList<>()).add(dead.getName());
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

        UUID playerId = player.getUniqueId();
        int currentPoints = playerPoints.getOrDefault(playerId, 0);
        playerPoints.put(playerId, currentPoints + pointsToAdd);

        String message = config.getString("messages.points-earned",
                "<green>You earned <points> points! Total: <total>");
        Component component = miniMessage.deserialize(message
                .replace("<points>", String.valueOf(pointsToAdd))
                .replace("<total>", String.valueOf(playerPoints.get(playerId))));
        player.sendMessage(component);

        plugin.getEffectManager().updatePlayerRank(player);
    }

    public Map<UUID, Integer> getPoints() {
        return playerPoints;
    }

    public int getPlayerPoints(UUID playerId) {
        return playerPoints.getOrDefault(playerId, 0);
    }

    public int getPlayerPlace(UUID playerId) {
        List<Map.Entry<UUID, Integer>> sortedPlayers = playerPoints.entrySet().stream()
                .sorted(Map.Entry.<UUID, Integer>comparingByValue().reversed())
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

        pointsConfig.createSection("Puntajes", playerPoints.entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey().toString(), Map.Entry::getValue)));

        pointsConfig.createSection("Top");
        List<Map.Entry<UUID, Integer>> sortedPlayers = playerPoints.entrySet().stream()
                .sorted(Map.Entry.<UUID, Integer>comparingByValue().reversed())
                .limit(10)
                .collect(Collectors.toList());

        for (int i = 0; i < sortedPlayers.size(); i++) {
            pointsConfig.set("Top." + (i + 1), sortedPlayers.get(i).getKey().toString());
        }

        try {
            pointsConfig.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save points to file: " + e.getMessage());
        }
    }

    public void loadRecord() {
        if (recordFile.exists()) {
            YamlConfiguration record = YamlConfiguration.loadConfiguration(recordFile);
            for (String key : record.getConfigurationSection("Points").getKeys(false)) {
                UUID uuid = UUID.fromString(key);
                playerPoints.put(uuid, record.getInt("Points." + key));
            }
            for (String key : record.getConfigurationSection("Ranks").getKeys(false)) {
                UUID uuid = UUID.fromString(key);
                playerRanks.put(uuid, record.getString("Ranks." + key));
            }
        }
    }

    public void saveRecord() {
        YamlConfiguration record = new YamlConfiguration();
        record.createSection("Points", playerPoints.entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey().toString(), Map.Entry::getValue)));
        record.createSection("Ranks", playerRanks.entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey().toString(), Map.Entry::getValue)));
        try {
            record.save(recordFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save records to file: " + e.getMessage());
        }
    }

    public void setPlayerRank(UUID playerId, String rank) {
        playerRanks.put(playerId, rank);
    }

    public String getPlayerRank(UUID playerId) {
        return playerRanks.getOrDefault(playerId, "none");
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public MiniMessage getMiniMessage() {
        return miniMessage;
    }
}

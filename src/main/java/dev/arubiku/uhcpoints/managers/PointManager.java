package dev.arubiku.uhcpoints.managers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private final Map<String, Integer> playerPoints = new HashMap<>();
    private final List<String> lastTwoDead = new ArrayList<>();
    private boolean firstKillDone = false;
    private final List<PointProvider> pointProviders = new ArrayList<>();
    private final FileConfiguration config;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    public PointManager(UHCPoints plugin) {
        this.plugin = plugin;
        this.config = loadConfig();
    }

    private FileConfiguration loadConfig() {
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
        }
        return YamlConfiguration.loadConfiguration(configFile);
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
            int points = provider.getPoints(player);
            if (points == 0)
                return;
            int currentPoints = playerPoints.getOrDefault(player.getName(), 0);
            playerPoints.put(player.getName(), currentPoints + points);

            String message = config.getString("messages.points-earned",
                    "<green>You earned <points> points! Total: <total>");
            Component component = miniMessage.deserialize(message
                    .replace("<points>", String.valueOf(points))
                    .replace("<total>", String.valueOf(playerPoints.get(player.getName()))));
            player.sendMessage(component);
        }
    }

    public Map<String, Integer> getPoints() {
        return playerPoints;
    }

    public int getPlayerPoints(String playerName) {
        return playerPoints.getOrDefault(playerName, 0);
    }

    public int getPlayerPlace(String playerName) {
        List<Map.Entry<String, Integer>> sortedPlayers = playerPoints.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toList());

        for (int i = 0; i < sortedPlayers.size(); i++) {
            if (sortedPlayers.get(i).getKey().equals(playerName)) {
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

        pointsConfig.createSection("Puntajes", playerPoints);

        pointsConfig.createSection("Top");
        List<Map.Entry<String, Integer>> sortedPlayers = playerPoints.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(10)
                .collect(Collectors.toList());

        for (int i = 0; i < sortedPlayers.size(); i++) {
            pointsConfig.set("Top." + (i + 1), sortedPlayers.get(i).getKey());
        }

        try {
            pointsConfig.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save points to file: " + e.getMessage());
        }
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public MiniMessage getMiniMessage() {
        return miniMessage;
    }
}

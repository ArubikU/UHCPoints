package dev.arubiku.uhcpoints.placeholders;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import dev.arubiku.uhcpoints.UHCPoints;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class UHCPointsPlaceholder extends PlaceholderExpansion {
    private final UHCPoints plugin;

    public UHCPointsPlaceholder(UHCPoints plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getIdentifier() {
        return "uhcpoints";
    }

    @Override
    public String getAuthor() {
        return "ArubikU";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        if (player == null) {
            return "";
        }

        if (identifier.equals("points")) {
            return String.valueOf(plugin.getPointManager().getPlayerPoints(player.getUniqueId()));
        }

        if (identifier.equals("ownplace")) {
            return String.valueOf(plugin.getPointManager().getPlayerPlace(player.getUniqueId()));
        }

        if (identifier.startsWith("place_")) {
            String[] parts = identifier.split("_");
            if (parts.length == 3) {
                int place = Integer.parseInt(parts[1]);
                List<Map.Entry<UUID, Integer>> sortedPlayers = plugin.getPointManager().getPoints().entrySet()
                        .stream()
                        .sorted(Map.Entry.<UUID, Integer>comparingByValue().reversed())
                        .collect(Collectors.toList());

                if (place <= sortedPlayers.size()) {
                    Map.Entry<UUID, Integer> entry = sortedPlayers.get(place - 1);
                    if (parts[2].equals("points")) {
                        return String.valueOf(entry.getValue());
                    } else if (parts[2].equals("name")) {
                        return Bukkit.getOfflinePlayer(entry.getKey()).getName();
                    }
                }
            }
        }

        return null;
    }
}

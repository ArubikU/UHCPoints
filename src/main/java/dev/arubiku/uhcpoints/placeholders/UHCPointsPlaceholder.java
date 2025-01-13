package dev.arubiku.uhcpoints.placeholders;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import dev.arubiku.uhcpoints.UHCPoints;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

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
            return String.valueOf(plugin.getPointManager().getPlayerPoints(player.getName()));
        }
        if (identifier.equals("recordpoints")) {
            return String.valueOf(plugin.getPointManager().getRecordPoints(player.getName()));
        }

        if (identifier.equals("ownplace")) {
            return String.valueOf(plugin.getPointManager().getPlayerPlace(player.getName()));
        }

        if (identifier.startsWith("rank")) {
            Component comp = plugin.getConfig().getRichMessage(
                    "ranks." + plugin.getEffectManager().getPlayerRank(player) + ".placeholder");

            if (identifier.equalsIgnoreCase("rankMini")) {
                return plugin.getConfig().getString(
                        "ranks." + plugin.getEffectManager().getPlayerRank(player) + ".placeholder");
            } else if (identifier.equalsIgnoreCase("rankAmps")) {
                return LegacyComponentSerializer.legacyAmpersand().serialize(comp);
            } else if (identifier.equalsIgnoreCase("rankSec")) {
                return LegacyComponentSerializer.legacySection().serialize(comp);
            } else {
                return plugin.getEffectManager().getPlayerRank(player);
            }

        }
        if (identifier.startsWith("place_")) {
            String[] parts = identifier.split("_");
            if (parts.length == 3) {
                int place = Integer.parseInt(parts[1]);
                List<Map.Entry<String, Integer>> sortedPlayers = plugin.getPointManager().getPoints().entrySet()
                        .stream()
                        .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                        .collect(Collectors.toList());

                if (place <= sortedPlayers.size()) {
                    Map.Entry<String, Integer> entry = sortedPlayers.get(place - 1);
                    if (parts[2].equals("points")) {
                        return String.valueOf(entry.getValue());
                    } else if (parts[2].equals("name")) {
                        return Bukkit.getOfflinePlayer(entry.getKey()).getName();
                    } else if (parts[2].startsWith("rank")) {
                        Component comp = plugin.getConfig().getRichMessage(
                                "ranks." + plugin.getEffectManager().getPlayerRank(entry.getKey()) + ".placeholder");

                        if (parts[2].equalsIgnoreCase("rankMini")) {
                            return plugin.getConfig().getString(
                                    "ranks." + plugin.getEffectManager().getPlayerRank(entry.getKey())
                                            + ".placeholder");
                        } else if (parts[2].equalsIgnoreCase("rankAmps")) {
                            return LegacyComponentSerializer.legacyAmpersand().serialize(comp);
                        } else if (parts[2].equalsIgnoreCase("rankSec")) {
                            return LegacyComponentSerializer.legacySection().serialize(comp);
                        } else {
                            return plugin.getEffectManager().getPlayerRank(entry.getKey());
                        }
                    }
                }
            }
        }

        return null;
    }
}

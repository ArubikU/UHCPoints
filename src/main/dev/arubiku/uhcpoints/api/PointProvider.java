package dev.arubiku.uhcpoints.api;

import org.bukkit.entity.Player;

public interface PointProvider {
    String getId();

    int getPoints(Player player);

    boolean shouldAwardPoints(Player player);
}

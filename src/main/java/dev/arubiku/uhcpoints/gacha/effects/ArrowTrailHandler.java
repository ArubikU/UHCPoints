package dev.arubiku.uhcpoints.gacha.effects;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;

@FunctionalInterface
public interface ArrowTrailHandler {
    void apply(Arrow arrow, Player player);
}
package dev.arubiku.uhcpoints.gacha.effects;

import org.bukkit.entity.Player;

@FunctionalInterface
public interface EffectHandler {
    void apply(Player player);
}

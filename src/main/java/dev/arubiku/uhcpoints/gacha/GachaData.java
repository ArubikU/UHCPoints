package dev.arubiku.uhcpoints.gacha;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;

public class GachaData {
    private static final Map<UUID, GachaData> playerData = new HashMap<>();

    private GachaEffect arrowTrailEffect;
    private GachaEffect deathEffect;
    private GachaEffect auraEffect;

    public static GachaData get(Player player) {
        return playerData.computeIfAbsent(player.getUniqueId(), k -> new GachaData());
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
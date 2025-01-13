package dev.arubiku.uhcpoints.gacha;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

import dev.arubiku.uhcpoints.gacha.effects.ArrowTrailHandler;
import dev.arubiku.uhcpoints.gacha.effects.EffectHandler;

public class GachaEffect {
    private static final Map<String, GachaEffect> BY_ID = new HashMap<>();

    // Arrow Trail Effects
    public static final GachaEffect FIRE_ARROW = new GachaEffect("FIRE_ARROW", EffectType.ARROW_TRAIL,
            (ArrowTrailHandler) (Arrow arrow, Player player) -> {
                arrow.setFireTicks(100);
                arrow.getWorld().spawnParticle(Particle.FLAME, arrow.getLocation(), 5, 0.1, 0.1, 0.1, 0.01);
            });

    public static final GachaEffect ICE_ARROW = new GachaEffect("ICE_ARROW", EffectType.ARROW_TRAIL,
            (ArrowTrailHandler) (Arrow arrow, Player player) -> {
                arrow.getWorld().spawnParticle(Particle.SNOWFLAKE, arrow.getLocation(), 5, 0.1, 0.1, 0.1, 0.01);
            });

    public static final GachaEffect RAINBOW_ARROW = new GachaEffect("RAINBOW_ARROW", EffectType.ARROW_TRAIL,
            (ArrowTrailHandler) (Arrow arrow, Player player) -> {
                Location loc = arrow.getLocation();
                for (int i = 0; i < 6; i++) {
                    arrow.getWorld().spawnParticle(Particle.DUST, loc, 10,
                            Color.fromRGB((int) (Math.random() * 255),
                                    (int) (Math.random() * 255), (int) (Math.random() * 255)));
                }
            });

    public static final GachaEffect MUSIC_ARROW = new GachaEffect("MUSIC_ARROW", EffectType.ARROW_TRAIL,
            (ArrowTrailHandler) (Arrow arrow, Player player) -> {
                arrow.getWorld().spawnParticle(Particle.NOTE, arrow.getLocation(), 1, 0.5, 0.5, 0.5, 1);
                arrow.getWorld().playSound(arrow.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, 1.0f, 1.0f);
            });

    public static final GachaEffect ENDER_ARROW = new GachaEffect("ENDER_ARROW", EffectType.ARROW_TRAIL,
            (ArrowTrailHandler) (Arrow arrow, Player player) -> {
                arrow.getWorld().spawnParticle(Particle.PORTAL, arrow.getLocation(), 10, 0.2, 0.2, 0.2, 0.1);
            });

    // Death Effects
    public static final GachaEffect EXPLOSION_DEATH = new GachaEffect("EXPLOSION_DEATH", EffectType.DEATH,
            (EffectHandler) player -> {
                player.getWorld().createExplosion(player.getLocation(), 0.0f, false, false);
                player.getWorld().spawnParticle(Particle.EXPLOSION, player.getLocation(), 1);
            });

    public static final GachaEffect LIGHTNING_DEATH = new GachaEffect("LIGHTNING_DEATH", EffectType.DEATH,
            (EffectHandler) player -> {
                player.getWorld().strikeLightningEffect(player.getLocation());
            });

    public static void spawnFireworks(Location location) {
        World world = location.getWorld();
        if (world == null)
            return;

        // Colores de los fuegos artificiales
        Color[] colors = { Color.RED, Color.GREEN, Color.BLUE };

        for (Color color : colors) {
            Firework firework = world.spawn(location, Firework.class);
            FireworkMeta meta = firework.getFireworkMeta();

            FireworkEffect effect = FireworkEffect.builder()
                    .withColor(color)
                    .withFade(Color.WHITE) // Efecto de desvanecimiento a blanco
                    .with(FireworkEffect.Type.BALL)
                    .trail(true)
                    .flicker(true)
                    .build();

            meta.addEffect(effect);
            meta.setPower(1); // Altura del fuego artificial
            firework.setFireworkMeta(meta);
        }
    }

    public static final GachaEffect FIREWORK_DEATH = new GachaEffect("FIREWORK_DEATH", EffectType.DEATH,
            (EffectHandler) player -> {
                player.getWorld().spawnParticle(Particle.FIREWORK, player.getLocation(), 50, 1, 1, 1, 0.1);
                spawnFireworks(player.getLocation());
            });

    public static final GachaEffect SOUL_DEATH = new GachaEffect("SOUL_DEATH", EffectType.DEATH,
            (EffectHandler) player -> {
                player.getWorld().spawnParticle(Particle.SOUL, player.getLocation().add(0, 1, 0), 20, 0.5, 1, 0.5, 0.1);
            });

    public static final GachaEffect TOTEM_DEATH = new GachaEffect("TOTEM_DEATH", EffectType.DEATH,
            (EffectHandler) player -> {
                player.getWorld().spawnParticle(Particle.TOTEM_OF_UNDYING, player.getLocation(), 100, 1, 2, 1, 0.5);
            });

    // Aura Effects
    public static final GachaEffect FLAME_AURA = new GachaEffect("FLAME_AURA", EffectType.AURA,
            (EffectHandler) player -> {
                player.getWorld().spawnParticle(Particle.FLAME, player.getLocation().add(0, 1, 0), 10, 0.3, 0.5, 0.3,
                        0.01);
            });

    public static final GachaEffect HEART_AURA = new GachaEffect("HEART_AURA", EffectType.AURA,
            (EffectHandler) player -> {
                player.getWorld().spawnParticle(Particle.HEART, player.getLocation().add(0, 2, 0), 1, 0.5, 0.5, 0.5, 0);
            });

    public static final GachaEffect ENCHANT_AURA = new GachaEffect("ENCHANT_AURA", EffectType.AURA,
            (EffectHandler) player -> {
                player.getWorld().spawnParticle(Particle.ENCHANT, player.getLocation().add(0, 1, 0), 20, 0.5, 0.5,
                        0.5, 1);
            });

    public static final GachaEffect SLIME_AURA = new GachaEffect("SLIME_AURA", EffectType.AURA,
            (EffectHandler) player -> {
                player.getWorld().spawnParticle(Particle.ITEM_SLIME, player.getLocation(), 10, 0.3, 0.1, 0.3, 0);
            });

    public static final GachaEffect GLITTER_AURA = new GachaEffect("GLITTER_AURA", EffectType.AURA,
            (EffectHandler) (Player player) -> {
                player.getWorld().spawnParticle(Particle.SCRAPE, player.getLocation().add(0, 1, 0), 20, 0.5, 0.5, 0.5,
                        0.1);
            });

    private final String id;
    private final EffectType type;
    private final Object handler;

    public GachaEffect(String id, EffectType type, Object handler) {
        this.id = id;
        this.type = type;
        this.handler = handler;
        BY_ID.put(id.toLowerCase(), this);
    }

    public static GachaEffect getById(String id) {
        return BY_ID.get(id.toLowerCase());
    }

    public static void registerCustomEffect(String id, EffectType type, Object handler) {
        new GachaEffect(id, type, handler);
    }

    public String getId() {
        return id;
    }

    public EffectType getType() {
        return type;
    }

    @SuppressWarnings("unchecked")
    public void apply(Object... args) {
        switch (type) {
            case ARROW_TRAIL:
                if (args.length == 2 && args[0] instanceof Arrow && args[1] instanceof Player) {
                    ((ArrowTrailHandler) handler).apply((Arrow) args[0], (Player) args[1]);
                }
                break;
            case DEATH:
            case AURA:
                if (args.length == 1 && args[0] instanceof Player) {
                    ((EffectHandler) handler).apply((Player) args[0]);
                }
                break;
        }
    }

    public static Collection<GachaEffect> values() {
        return BY_ID.values();
    }

    public enum EffectType {
        ARROW_TRAIL,
        DEATH,
        AURA
    }
}
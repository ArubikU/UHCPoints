package dev.arubiku.uhcpoints.gacha;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
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

        public static DustOptions fromColor(Color col) {
                return new DustOptions(col, 2);
        }

        // Existing Arrow Trail Effects
        public static final GachaEffect FIRE_ARROW = new GachaEffect("FIRE_ARROW", EffectType.ARROW_TRAIL,
                        (ArrowTrailHandler) (Arrow arrow, Player player) -> {
                                arrow.setFireTicks(100);
                                arrow.getWorld().spawnParticle(Particle.FLAME, arrow.getLocation(), 5, 0.1, 0.1, 0.1,
                                                0.01);
                        });

        public static final GachaEffect ICE_ARROW = new GachaEffect("ICE_ARROW", EffectType.ARROW_TRAIL,
                        (ArrowTrailHandler) (Arrow arrow, Player player) -> {
                                arrow.getWorld().spawnParticle(Particle.SNOWFLAKE, arrow.getLocation(), 5, 0.1, 0.1,
                                                0.1, 0.01);
                        });

        public static final GachaEffect RAINBOW_ARROW = new GachaEffect("RAINBOW_ARROW", EffectType.ARROW_TRAIL,
                        (ArrowTrailHandler) (Arrow arrow, Player player) -> {
                                Location loc = arrow.getLocation();
                                for (int i = 0; i < 6; i++) {
                                        arrow.getWorld().spawnParticle(Particle.DUST, loc, 5,
                                                        fromColor(Color.fromRGB((int) (Math.random() * 255),
                                                                        (int) (Math.random() * 255),
                                                                        (int) (Math.random() * 255))));
                                }
                        });

        public static final GachaEffect MUSIC_ARROW = new GachaEffect("MUSIC_ARROW", EffectType.ARROW_TRAIL,
                        (ArrowTrailHandler) (Arrow arrow, Player player) -> {
                                arrow.getWorld().spawnParticle(Particle.NOTE, arrow.getLocation(), 1, 0.3, 0.3, 0.3, 1);
                                arrow.getWorld().playSound(arrow.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, 1.0f,
                                                1.0f);
                        });

        public static final GachaEffect ENDER_ARROW = new GachaEffect("ENDER_ARROW", EffectType.ARROW_TRAIL,
                        (ArrowTrailHandler) (Arrow arrow, Player player) -> {
                                arrow.getWorld().spawnParticle(Particle.PORTAL, arrow.getLocation(), 10, 0.2, 0.2, 0.2,
                                                0.1);
                        });

        // New Arrow Trail Effects
        public static final GachaEffect LIGHTNING_ARROW = new GachaEffect("LIGHTNING_ARROW", EffectType.ARROW_TRAIL,
                        (ArrowTrailHandler) (Arrow arrow, Player player) -> {
                                if (arrow.isInBlock())
                                        return;
                                arrow.getWorld().strikeLightningEffect(arrow.getLocation());
                        });

        public static final GachaEffect SMOKE_ARROW = new GachaEffect("SMOKE_ARROW", EffectType.ARROW_TRAIL,
                        (ArrowTrailHandler) (Arrow arrow, Player player) -> {
                                arrow.getWorld().spawnParticle(Particle.SMOKE, arrow.getLocation(), 10, 0.1, 0.1, 0.1,
                                                0.01);
                        });

        public static final GachaEffect WATER_ARROW = new GachaEffect("WATER_ARROW", EffectType.ARROW_TRAIL,
                        (ArrowTrailHandler) (Arrow arrow, Player player) -> {
                                arrow.getWorld().spawnParticle(Particle.DRIPPING_WATER, arrow.getLocation(), 15, 0.1,
                                                0.1, 0.1, 0.1);
                        });

        public static final GachaEffect LAVA_ARROW = new GachaEffect("LAVA_ARROW", EffectType.ARROW_TRAIL,
                        (ArrowTrailHandler) (Arrow arrow, Player player) -> {
                                arrow.getWorld().spawnParticle(Particle.LAVA, arrow.getLocation(), 1, 0.1, 0.1, 0.1, 0);
                        });

        public static final GachaEffect REDSTONE_ARROW = new GachaEffect("REDSTONE_ARROW", EffectType.ARROW_TRAIL,
                        (ArrowTrailHandler) (Arrow arrow, Player player) -> {
                                arrow.getWorld().spawnParticle(Particle.DUST, arrow.getLocation(), 10, 0.1, 0.1, 0.1, 1,
                                                fromColor(Color.RED));
                        });

        public static final GachaEffect SPARKLE_ARROW = new GachaEffect("SPARKLE_ARROW", EffectType.ARROW_TRAIL,
                        (ArrowTrailHandler) (Arrow arrow, Player player) -> {
                                arrow.getWorld().spawnParticle(Particle.FIREWORK, arrow.getLocation(), 5, 0.1, 0.1, 0.1,
                                                0.01);
                        });

        public static final GachaEffect HEART_ARROW = new GachaEffect("HEART_ARROW", EffectType.ARROW_TRAIL,
                        (ArrowTrailHandler) (Arrow arrow, Player player) -> {
                                arrow.getWorld().spawnParticle(Particle.HEART, arrow.getLocation(), 1, 0.1, 0.1, 0.1,
                                                0);
                        });

        public static final GachaEffect SOUL_ARROW = new GachaEffect("SOUL_ARROW", EffectType.ARROW_TRAIL,
                        (ArrowTrailHandler) (Arrow arrow, Player player) -> {
                                arrow.getWorld().spawnParticle(Particle.SOUL, arrow.getLocation(), 5, 0.1, 0.1, 0.1,
                                                0.01);
                        });

        public static final GachaEffect ENCHANT_ARROW = new GachaEffect("ENCHANT_ARROW", EffectType.ARROW_TRAIL,
                        (ArrowTrailHandler) (Arrow arrow, Player player) -> {
                                arrow.getWorld().spawnParticle(Particle.ENCHANTED_HIT, arrow.getLocation(), 10, 0.1,
                                                0.1, 0.1, 0.5);
                        });

        public static final GachaEffect TOTEM_ARROW = new GachaEffect("TOTEM_ARROW", EffectType.ARROW_TRAIL,
                        (ArrowTrailHandler) (Arrow arrow, Player player) -> {
                                arrow.getWorld().spawnParticle(Particle.TOTEM_OF_UNDYING, arrow.getLocation(), 10, 0.1,
                                                0.1, 0.1, 0.5);
                        });

        // Existing Death Effects
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
                                player.getWorld().spawnParticle(Particle.FIREWORK, player.getLocation(), 50, 1, 1, 1,
                                                0.1);
                                spawnFireworks(player.getLocation());
                        });

        public static final GachaEffect SOUL_DEATH = new GachaEffect("SOUL_DEATH", EffectType.DEATH,
                        (EffectHandler) player -> {
                                player.getWorld().spawnParticle(Particle.SOUL, player.getLocation().add(0, 0.8, 0), 20,
                                                0.3, 1, 0.3,
                                                0.1);
                        });

        public static final GachaEffect TOTEM_DEATH = new GachaEffect("TOTEM_DEATH", EffectType.DEATH,
                        (EffectHandler) player -> {
                                player.getWorld().spawnParticle(Particle.TOTEM_OF_UNDYING, player.getLocation(), 100, 1,
                                                2, 1, 0.3);
                        });

        // New Death Effects
        public static final GachaEffect SMOKE_DEATH = new GachaEffect("SMOKE_DEATH", EffectType.DEATH,
                        (EffectHandler) player -> {
                                player.getWorld().spawnParticle(Particle.SMOKE, player.getLocation(), 100, 1, 1, 1,
                                                0.1);
                        });

        public static final GachaEffect ENDER_DEATH = new GachaEffect("ENDER_DEATH", EffectType.DEATH,
                        (EffectHandler) player -> {
                                player.getWorld().spawnParticle(Particle.PORTAL, player.getLocation(), 100, 1, 1, 1, 1);
                                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f,
                                                1.0f);
                        });

        public static final GachaEffect WATER_DEATH = new GachaEffect("WATER_DEATH", EffectType.DEATH,
                        (EffectHandler) player -> {
                                player.getWorld().spawnParticle(Particle.DRIPPING_DRIPSTONE_WATER, player.getLocation(),
                                                200, 1, 1, 1,
                                                1);
                                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_SPLASH, 1.0f,
                                                1.0f);
                        });

        public static final GachaEffect LAVA_DEATH = new GachaEffect("LAVA_DEATH", EffectType.DEATH,
                        (EffectHandler) player -> {
                                player.getWorld().spawnParticle(Particle.LAVA, player.getLocation(), 50, 1, 1, 1, 0.1);
                                player.getWorld().playSound(player.getLocation(), Sound.BLOCK_LAVA_POP, 1.0f, 1.0f);
                        });

        public static final GachaEffect HEART_DEATH = new GachaEffect("HEART_DEATH", EffectType.DEATH,
                        (EffectHandler) player -> {
                                player.getWorld().spawnParticle(Particle.HEART, player.getLocation(), 50, 1, 1, 1, 0.1);
                        });

        public static final GachaEffect SNOW_DEATH = new GachaEffect("SNOW_DEATH", EffectType.DEATH,
                        (EffectHandler) player -> {
                                player.getWorld().spawnParticle(Particle.SNOWFLAKE, player.getLocation(), 100, 1, 1, 1,
                                                0.1);
                                player.getWorld().playSound(player.getLocation(), Sound.BLOCK_SNOW_BREAK, 1.0f, 1.0f);
                        });

        public static final GachaEffect MUSIC_DEATH = new GachaEffect("MUSIC_DEATH", EffectType.DEATH,
                        (EffectHandler) player -> {
                                player.getWorld().spawnParticle(Particle.NOTE, player.getLocation(), 50, 1, 1, 1, 1);
                                player.getWorld().playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, 1.0f,
                                                1.0f);
                        });

        public static final GachaEffect DRAGON_DEATH = new GachaEffect("DRAGON_DEATH", EffectType.DEATH,
                        (EffectHandler) player -> {
                                player.getWorld().spawnParticle(Particle.DRAGON_BREATH, player.getLocation(), 100, 1, 1,
                                                1, 0.1);
                                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0f,
                                                1.0f);
                        });

        public static final GachaEffect RAINBOW_DEATH = new GachaEffect("RAINBOW_DEATH", EffectType.DEATH,
                        (EffectHandler) player -> {
                                for (int i = 0; i < 7; i++) {
                                        player.getWorld().spawnParticle(Particle.DUST, player.getLocation(), 50, 1, 1,
                                                        1,
                                                        fromColor(Color.fromRGB((int) (Math.random() * 255),
                                                                        (int) (Math.random() * 255),
                                                                        (int) (Math.random() * 255))));
                                }
                        });

        public static final GachaEffect VOID_DEATH = new GachaEffect("VOID_DEATH", EffectType.DEATH,
                        (EffectHandler) player -> {
                                player.getWorld().spawnParticle(Particle.REVERSE_PORTAL, player.getLocation(), 100, 1,
                                                1, 1, 0.1);
                                player.getWorld().playSound(player.getLocation(), Sound.BLOCK_END_PORTAL_SPAWN, 1.0f,
                                                1.0f);
                        });

        // Existing Aura Effects
        public static final GachaEffect FLAME_AURA = new GachaEffect("FLAME_AURA", EffectType.AURA,
                        (EffectHandler) player -> {
                                player.getWorld().spawnParticle(Particle.FLAME, player.getLocation().add(0, 0.8, 0), 10,
                                                0.3, 0.3, 0.3,
                                                0.01);
                        });

        public static final GachaEffect HEART_AURA = new GachaEffect("HEART_AURA", EffectType.AURA,
                        (EffectHandler) player -> {
                                player.getWorld().spawnParticle(Particle.HEART, player.getLocation().add(0, 1 / 8, 0),
                                                1, 0.3, 0.3, 0.3,
                                                0);
                        });

        public static final GachaEffect ENCHANT_AURA = new GachaEffect("ENCHANT_AURA", EffectType.AURA,
                        (EffectHandler) player -> {
                                player.getWorld().spawnParticle(Particle.ENCHANT, player.getLocation().add(0, 0.8, 0),
                                                20, 0.3, 0.3,
                                                0.3, 1);
                        });

        public static final GachaEffect SLIME_AURA = new GachaEffect("SLIME_AURA", EffectType.AURA,
                        (EffectHandler) player -> {
                                player.getWorld().spawnParticle(Particle.ITEM_SLIME, player.getLocation(), 10, 0.3, 0.1,
                                                0.3, 0);
                        });

        public static final GachaEffect GLITTER_AURA = new GachaEffect("GLITTER_AURA", EffectType.AURA,
                        (EffectHandler) (Player player) -> {
                                player.getWorld().spawnParticle(Particle.SCRAPE, player.getLocation().add(0, 0.8, 0),
                                                20, 0.3, 0.3, 0.3,
                                                0.1);
                        });

        // New Aura Effects
        public static final GachaEffect SMOKE_AURA = new GachaEffect("SMOKE_AURA", EffectType.AURA,
                        (EffectHandler) player -> {
                                player.getWorld().spawnParticle(Particle.SMOKE, player.getLocation().add(0, 0.8, 0), 10,
                                                0.3,
                                                0.3, 0.3, 0.01);
                        });

        public static final GachaEffect WATER_AURA = new GachaEffect("WATER_AURA", EffectType.AURA,
                        (EffectHandler) player -> {
                                player.getWorld().spawnParticle(Particle.DRIPPING_WATER,
                                                player.getLocation().add(0, 0.8, 0), 10, 0.3,
                                                0.3,
                                                0.3, 0.1);
                        });

        public static final GachaEffect ENDER_AURA = new GachaEffect("ENDER_AURA", EffectType.AURA,
                        (EffectHandler) player -> {
                                player.getWorld().spawnParticle(Particle.PORTAL, player.getLocation().add(0, 0.8, 0),
                                                20, 0.3, 0.3, 0.3,
                                                0.1);
                        });

        public static final GachaEffect REDSTONE_AURA = new GachaEffect("REDSTONE_AURA", EffectType.AURA,
                        (EffectHandler) player -> {
                                player.getWorld().spawnParticle(Particle.DUST, player.getLocation().add(0, 0.8, 0), 10,
                                                0.3, 0.3,
                                                0.3, 1, fromColor(Color.RED));
                        });

        public static final GachaEffect SOUL_AURA = new GachaEffect("SOUL_AURA", EffectType.AURA,
                        (EffectHandler) player -> {
                                player.getWorld().spawnParticle(Particle.SOUL, player.getLocation().add(0, 0.8, 0), 10,
                                                0.3, 0.3, 0.3,
                                                0.01);
                        });

        public static final GachaEffect TOTEM_AURA = new GachaEffect("TOTEM_AURA", EffectType.AURA,
                        (EffectHandler) player -> {
                                player.getWorld().spawnParticle(Particle.TOTEM_OF_UNDYING,
                                                player.getLocation().add(0, 0.8, 0), 20, 0.3,
                                                0.3, 0.3,
                                                0.1);
                        });

        public static final GachaEffect SNOW_AURA = new GachaEffect("SNOW_AURA", EffectType.AURA,
                        (EffectHandler) player -> {
                                player.getWorld().spawnParticle(Particle.SNOWFLAKE, player.getLocation().add(0, 0.8, 0),
                                                10, 0.3, 0.3,
                                                0.3, 0.01);
                        });

        public static final GachaEffect MUSIC_AURA = new GachaEffect("MUSIC_AURA", EffectType.AURA,
                        (EffectHandler) player -> {
                                player.getWorld().spawnParticle(Particle.NOTE, player.getLocation().add(0, 0.8, 0), 5,
                                                0.3, 0.3, 0.3,
                                                1);
                        });

        public static final GachaEffect DRAGON_AURA = new GachaEffect("DRAGON_AURA", EffectType.AURA,
                        (EffectHandler) player -> {
                                player.getWorld().spawnParticle(Particle.DRAGON_BREATH,
                                                player.getLocation().add(0, 0.8, 0), 20, 0.3,
                                                0.3, 0.3, 0.01);
                        });

        public static final GachaEffect RAINBOW_AURA = new GachaEffect("RAINBOW_AURA", EffectType.AURA,
                        (EffectHandler) player -> {
                                for (int i = 0; i < 7; i++) {
                                        player.getWorld().spawnParticle(Particle.DUST,
                                                        player.getLocation().add(0, 0.8, 0), 10, 0.3, 0.3,
                                                        0.3,
                                                        fromColor(Color.fromRGB((int) (Math.random() * 255),
                                                                        (int) (Math.random() * 255),
                                                                        (int) (Math.random() * 255))));
                                }
                        });

        private final String id;
        private final EffectType type;
        private final Object handler;

        public GachaEffect(String id, EffectType type, Object handler) {
                this.id = id.toLowerCase();
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

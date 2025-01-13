package dev.arubiku.uhcpoints.effects;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import dev.arubiku.uhcpoints.UHCPoints;
import dev.arubiku.uhcpoints.listeners.UHCPointsListener;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class EffectManager {
    private static UHCPoints plugin;
    private final Map<String, String> playerRanks = new HashMap<>();
    public static final Set<String> trail = new HashSet<>();
    public static final Set<String> aura = new HashSet<>();

    public EffectManager(UHCPoints plugin) {
        EffectManager.plugin = plugin;
    }

    public enum Effect {
        MESSAGE("message", (player, data) -> {
            Component comp = MiniMessage.miniMessage().deserialize(data);
            if (plugin.getPointManager().playerKills.containsKey(player.getName())) {
                data.replace("%killed%", UHCPoints.getPlugin(UHCPoints.class).getPointManager().playerKills
                        .get(player.getName()).getLast());
            }
            comp = comp.replaceText(builder -> builder.match("%player%").replacement(player.getName()));
            player.sendMessage(comp);
        }),

        MESSAGE_ALL("message-all", (player, data) -> {
            Component comp = MiniMessage.miniMessage().deserialize(data);
            comp = comp.replaceText(builder -> builder.match("%player%").replacement(player.getName()));
            Bukkit.broadcast(comp);
        }),
        PARTICLE("particle", (player, data) -> {
            try {
                String[] parts = data.split(":");
                Particle particle = Particle.valueOf(parts[0].toUpperCase());
                int count = parts.length > 1 ? Integer.parseInt(parts[1]) : 50;
                if (particle.getDataType() == Material.class) {
                    Material mat = Material.valueOf(parts.length > 2 ? parts[2] : "STONE");

                    player.getWorld().spawnParticle(particle, player.getLocation(), count,
                            mat);

                } else if (particle.getDataType() == Color.class) {

                    int r = parts.length > 2 ? Integer.parseInt(parts[2]) : 50;
                    int g = parts.length > 3 ? Integer.parseInt(parts[3]) : 50;
                    int b = parts.length > 4 ? Integer.parseInt(parts[4]) : 50;
                    Color color = Color.fromRGB(r, g, b);

                    player.getWorld().spawnParticle(particle, player.getLocation(), count,
                            color);

                } else {

                    player.getWorld().spawnParticle(particle, player.getLocation(), count);
                }
            } catch (Throwable e) {
                plugin.getLogger().warning("Invalid particle type: " + data);
            }
        }),
        SOUND("sound", (player, data) -> {
            try {
                String[] parts = data.split(":");
                Sound sound = RegistryAccess.registryAccess().getRegistry(RegistryKey.SOUND_EVENT)
                        .get(Key.key(parts[0].toUpperCase()));
                if (sound == null)
                    return;
                float volume = parts.length > 1 ? Float.parseFloat(parts[1]) : 1.0f;
                float pitch = parts.length > 2 ? Float.parseFloat(parts[2]) : 1.0f;
                player.playSound(player.getLocation(), sound, volume, pitch);
            } catch (Throwable e) {
                plugin.getLogger().warning("Invalid sound: " + data);
            }
        }),
        PARTICLE_TRAIL("particle_trail", (player, data) -> {
            try {
                if (EffectManager.trail.contains(player.getName()))
                    return;
                String[] parts = data.split(":");
                Particle particle = Particle.valueOf(parts[0].toUpperCase());
                int duration = parts.length > 1 ? Integer.parseInt(parts[1]) : 100;
                new BukkitRunnable() {
                    int ticks = 0;

                    @Override
                    public void run() {
                        if (ticks >= duration || !player.isOnline()) {
                            this.cancel();
                            EffectManager.trail.remove(player.getName());
                            return;
                        }
                        player.getWorld().spawnParticle(particle, player.getLocation(), 1, 0, 0, 0, 0);
                        ticks++;
                    }
                }.runTaskTimer(plugin, 0L, 1L);
                EffectManager.trail.add(player.getName());
            } catch (Throwable e) {
                plugin.getLogger().warning("Invalid particle trail: " + data);
            }
        }),
        MINING_EFFECT("mining_effect", (player, data) -> {
            try {
                String[] parts = data.split(":");
                Particle particle = Particle.valueOf(parts[0].toUpperCase());
                if (UHCPointsListener.lastBlock.containsKey(player.getName())) {
                    Location loc = UHCPointsListener.lastBlock.get(player.getName());
                    loc.getWorld().spawnParticle(particle, loc, 50);
                }
                if (parts.length > 1) {

                    Sound sound = RegistryAccess.registryAccess().getRegistry(RegistryKey.SOUND_EVENT)
                            .get(Key.key(parts[1].toUpperCase()));
                    if (sound == null)
                        return;
                    float volume = parts.length > 2 ? Float.parseFloat(parts[2]) : 1.0f;
                    float pitch = parts.length > 3 ? Float.parseFloat(parts[3]) : 1.0f;
                    player.playSound(player.getLocation(), sound, volume, pitch);
                }
            } catch (Throwable e) {
                plugin.getLogger().warning("Invalid mining effect: " + data);
            }
        }),
        AURA("aura", (player, data) -> {
            try {
                if (EffectManager.aura.contains(player.getName()))
                    return;
                String[] parts = data.split(":");
                Particle particle = Particle.valueOf(parts[0].toUpperCase());
                double radius = parts.length > 1 ? Double.parseDouble(parts[1]) : 0.5;
                int count = parts.length > 2 ? Integer.parseInt(parts[2]) : 10;
                new BukkitRunnable() {

                    @Override
                    public void run() {
                        if (!player.isOnline()) {
                            this.cancel();
                            EffectManager.aura.remove(player.getName());
                            return;
                        }
                        Location loc = player.getLocation();
                        for (int i = 0; i < count; i++) {
                            double angle = 2 * Math.PI * i / count;
                            double x = Math.cos(angle) * radius;
                            double z = Math.sin(angle) * radius;
                            loc.add(x, 1, z);
                            player.getWorld().spawnParticle(particle, loc, 1, 0, 0, 0, 0);
                            loc.subtract(x, 1, z);
                        }
                    }
                }.runTaskTimer(plugin, 0L, 5L);

                EffectManager.aura.add(player.getName());
            } catch (

        Throwable e) {
                plugin.getLogger().warning("Invalid aura effect: " + data);
            }
        });

        public final String key;
        public final BiConsumer<Player, String> executor;

        Effect(String key, BiConsumer<Player, String> executor) {
            this.key = key;
            this.executor = executor;
        }

    }

    public void executeEffect(Player player, String effectKey, String data) {
        for (Effect effect : Effect.values()) {
            if (effect.key.equalsIgnoreCase(effectKey)) {
                effect.executor.accept(player, data);
                break;
            }
        }
    }

    public void updatePlayerRank(Player player) {
        int points = plugin.getPointManager().getRecordPoints(player.getName());
        String newRank = "none";

        ConfigurationSection ranksSection = plugin.getConfig().getConfigurationSection("ranks");
        if (ranksSection != null) {
            for (String rank : ranksSection.getKeys(false)) {
                int minPoints = ranksSection.getInt(rank + ".points.min");
                int maxPoints = ranksSection.getInt(rank + ".points.max");

                if (points >= minPoints && points <= maxPoints) {
                    newRank = rank;
                    break;
                }
            }
        }

        String oldRank = playerRanks.get(player.getName());
        if (oldRank == null || !oldRank.equals(newRank)) {
            playerRanks.put(player.getName(), newRank);
            executeRankEffects(player, "onRankup");
        }
    }

    public void executeRankEffects(Player player, String trigger) {
        String rank = playerRanks.get(player.getName());
        if (rank == null)
            return;

        String path = "ranks." + rank + ".effects." + trigger;
        if (plugin.getConfig().contains(path)) {
            for (String effectKey : plugin.getConfig().getConfigurationSection(path).getKeys(false)) {
                String data = plugin.getConfig().getString(path + "." + effectKey);
                executeEffect(player, effectKey, data);
            }
        }
    }

    public String getPlayerRank(Player player) {
        return playerRanks.getOrDefault(player.getName(), "none");
    }

    public String getPlayerRank(String player) {
        return playerRanks.getOrDefault(player, "none");
    }

    public void loadPlayerRanks() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            updatePlayerRank(player);
        }
    }

    public void savePlayerRanks() {
        for (Map.Entry<String, String> entry : playerRanks.entrySet()) {
            plugin.getPointManager().setPlayerRank(entry.getKey(), entry.getValue());
        }
    }
}

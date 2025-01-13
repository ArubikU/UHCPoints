package dev.arubiku.uhcpoints.listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import dev.arubiku.uhcpoints.UHCPoints;
import dev.arubiku.uhcpoints.api.PointProvider;

public class UHCPointsListener implements Listener {
    private final UHCPoints plugin;

    public UHCPointsListener(UHCPoints plugin) {
        this.plugin = plugin;
        registerDefaultPointProviders();
    }

    private void registerDefaultPointProviders() {
        plugin.getPointManager().registerPointProvider(new PointProvider() {
            @Override
            public String getId() {
                return "game_start";
            }

            @Override
            public int getPoints(Player player) {
                return plugin.getPointManager().getConfig().getInt("points.game-start", 50);
            }

            @Override
            public boolean shouldAwardPoints(Player player) {
                return true;
            }
        });
        plugin.getPointManager().registerPointProvider(new PointProvider() {
            @Override
            public String getId() {
                return "first_kill_lobby";
            }

            @Override
            public int getPoints(Player player) {
                return plugin.getPointManager().getConfig().getInt("points.first-kill-lobby", 20);
            }

            @Override
            public boolean shouldAwardPoints(Player player) {
                return !plugin.getPointManager().isFirstKillDone();
            }
        });
        plugin.getPointManager().registerPointProvider(new PointProvider() {
            @Override
            public String getId() {
                return "first_kill";
            }

            @Override
            public int getPoints(Player player) {
                return plugin.getPointManager().getConfig().getInt("points.first-kill", 50);
            }

            @Override
            public boolean shouldAwardPoints(Player player) {
                return plugin.getPointManager().isFirstKill(player);
            }
        });

        plugin.getPointManager().registerPointProvider(new PointProvider() {
            @Override
            public String getId() {
                return "kill";
            }

            @Override
            public int getPoints(Player player) {
                return plugin.getPointManager().getConfig().getInt("points.kill", 30);
            }

            @Override
            public boolean shouldAwardPoints(Player player) {
                return true;
            }
        });

        plugin.getPointManager().registerPointProvider(new PointProvider() {
            @Override
            public String getId() {
                return "notch_apple";
            }

            @Override
            public int getPoints(Player player) {
                return plugin.getPointManager().getConfig().getInt("points.notch-apple", 5);
            }

            @Override
            public boolean shouldAwardPoints(Player player) {
                return true;
            }
        });

        plugin.getPointManager().registerPointProvider(new PointProvider() {
            @Override
            public String getId() {
                return "high_level_enchant";
            }

            @Override
            public int getPoints(Player player) {
                return plugin.getPointManager().getConfig().getInt("points.high-level-enchant", 5);
            }

            @Override
            public boolean shouldAwardPoints(Player player) {
                return true;
            }
        });

        plugin.getPointManager().registerPointProvider(new PointProvider() {
            @Override
            public String getId() {
                return "first_place";
            }

            @Override
            public int getPoints(Player player) {
                return plugin.getPointManager().getConfig().getInt("points.first-place", 100);
            }

            @Override
            public boolean shouldAwardPoints(Player player) {
                return true;
            }
        });

        plugin.getPointManager().registerPointProvider(new PointProvider() {
            @Override
            public String getId() {
                return "second_place";
            }

            @Override
            public int getPoints(Player player) {
                return plugin.getPointManager().getConfig().getInt("points.second-place", 60);
            }

            @Override
            public boolean shouldAwardPoints(Player player) {
                return true;
            }
        });

        plugin.getPointManager().registerPointProvider(new PointProvider() {
            @Override
            public String getId() {
                return "third_place";
            }

            @Override
            public int getPoints(Player player) {
                return plugin.getPointManager().getConfig().getInt("points.third-place", 20);
            }

            @Override
            public boolean shouldAwardPoints(Player player) {
                return true;
            }
        });
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        plugin.getEffectManager().updatePlayerRank(player);
    }

    public static List<String> deadPlayers = new ArrayList<>();

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player killed = event.getEntity();
        Player killer = killed.getKiller();
        if (killer == null) {
            if (event.getEntity().getLastDamageCause().getDamageSource().getCausingEntity() instanceof Player kil) {
                killer = kil;
            }
        }
        if (killer != null) {
            if (!plugin.getPointManager().isFirstKillDone()) {
                plugin.getPointManager().addPoints(killer, "first_kill_lobby");
                plugin.getPointManager().setFirstKillDone();
            }
            if (plugin.getPointManager().isFirstKill(killer)) {
                plugin.getPointManager().addPoints(killer, "first_kill");
            } else {
                plugin.getPointManager().addPoints(killer, "kill");
            }
            plugin.getPointManager().addKill(killer, killed);
            plugin.getEffectManager().executeRankEffects(killer, "onKill");
        }

        plugin.getPointManager().addLastDead(killed.getName());
        deadPlayers.add(event.getPlayer().getName());
    }

    @EventHandler
    public void onItemConsume(PlayerItemConsumeEvent event) {
        if (event.getItem().getType() == Material.GOLDEN_APPLE) {
            plugin.getPointManager().addPoints(event.getPlayer(), "notch_apple");
        }
    }

    @EventHandler
    public void onEnchantItem(EnchantItemEvent event) {
        if (event.getExpLevelCost() >= 15) {
            plugin.getPointManager().addPoints(event.getEnchanter(), "high_level_enchant");
        }
    }

    public static Map<String, Location> lastBlock = new HashMap<>();

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        lastBlock.put(player.getName(), event.getBlock().getLocation());
        plugin.getEffectManager().executeRankEffects(player, "mining_effect");
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        plugin.getEffectManager().executeRankEffects(player, "aura");
        plugin.getEffectManager().executeRankEffects(player, "particle_trail");
    }

}

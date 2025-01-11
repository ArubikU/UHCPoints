package dev.arubiku.uhcpoints.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;

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
                return "first_kill";
            }

            @Override
            public int getPoints(Player player) {
                return plugin.getPointManager().getConfig().getInt("points.first-kill", 50);
            }

            @Override
            public boolean shouldAwardPoints(Player player) {
                return !plugin.getPointManager().isFirstKillDone();
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
                return plugin.getPointManager().getConfig().getInt("points.first-place", 5);
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
                return plugin.getPointManager().getConfig().getInt("points.second-place", 5);
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
                return plugin.getPointManager().getConfig().getInt("points.third-place", 5);
            }

            @Override
            public boolean shouldAwardPoints(Player player) {
                return true;
            }
        });
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if (killer != null) {
            if (!plugin.getPointManager().isFirstKillDone()) {
                plugin.getPointManager().addPoints(killer, "first_kill");
                plugin.getPointManager().setFirstKillDone();
            } else {
                plugin.getPointManager().addPoints(killer, "kill");
            }
        }

        plugin.getPointManager().addLastDead(event.getEntity().getName());
    }

    @EventHandler
    public void onItemConsume(PlayerItemConsumeEvent event) {
        if (event.getItem().getType() == Material.ENCHANTED_GOLDEN_APPLE) {
            plugin.getPointManager().addPoints(event.getPlayer(), "notch_apple");
        }
    }

    @EventHandler
    public void onEnchantItem(EnchantItemEvent event) {
        if (event.getExpLevelCost() >= 30) {
            plugin.getPointManager().addPoints(event.getEnchanter(), "high_level_enchant");
        }
    }
}

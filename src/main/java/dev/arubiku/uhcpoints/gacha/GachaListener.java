package dev.arubiku.uhcpoints.gacha;

import java.util.Map;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class GachaListener implements Listener {
    private final GachaponManager plugin;

    public GachaListener(GachaponManager plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Initialize player data if necessary
        plugin.getGachaDataManager().getPlayerData(event.getPlayer());
    }

    @EventHandler
    public void onEntityShootBow(EntityShootBowEvent event) {
        if (event.getEntity() instanceof Player && event.getProjectile() instanceof Arrow) {
            Player player = (Player) event.getEntity();
            Arrow arrow = (Arrow) event.getProjectile();
            GachaEffect arrowTrail = plugin.getGachaDataManager().getPlayerData(player).getArrowTrailEffect();
            if (arrowTrail != null) {
                arrowTrail.apply(arrow, player);
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        GachaEffect deathEffect = plugin.getGachaDataManager().getPlayerData(player).getDeathEffect();
        if (deathEffect != null) {
            deathEffect.apply(player);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        GachaEffect auraEffect = plugin.getGachaDataManager().getPlayerData(player).getAuraEffect();
        if (auraEffect != null) {
            auraEffect.apply(player);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player))
            return;
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getInventory();
        ItemStack clickedItem = event.getCurrentItem();

        if (clickedItem == null)
            return;

        if (inventory.getHolder() instanceof GachaGui.MainMenuHolder) {
            event.setCancelled(true);
            handleMainMenuClick(player, clickedItem);
        } else if (inventory.getHolder() instanceof GachaGui.EquipMenuHolder) {
            event.setCancelled(true);
            handleEquipMenuClick(player, clickedItem);
        }
    }

    private void handleMainMenuClick(Player player, ItemStack clickedItem) {
        GachaConfigManager.GUIConfig guiConfig = plugin.getGachaConfigManager().getGUIConfig("main_menu");
        for (Map.Entry<Integer, GachaConfigManager.GUIItem> entry : guiConfig.getItems().entrySet()) {
            if (clickedItem.isSimilar(plugin.getGachaGUI().createGuiItem(entry.getValue()))) {
                switch (entry.getValue().getName()) {
                    case "Girar Gachapón":
                        plugin.command.handleSpin(player);
                        break;
                    case "Equipar Efectos":
                        plugin.getGachaGUI().openEquipMenu(player);
                        break;
                }
                break;
            }
        }
    }

    private void handleEquipMenuClick(Player player, ItemStack clickedItem) {
        GachaEffect effect = getEffectFromItem(clickedItem);
        if (effect != null) {
            equipEffect(player, effect);
        }
    }

    private GachaEffect getEffectFromItem(ItemStack item) {
        if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
            String name = item.getItemMeta().getDisplayName();
            for (GachaEffect effect : GachaEffect.values()) {
                if (name.contains(effect.getId())) {
                    return effect;
                }
            }
        }
        return null;
    }

    private void equipEffect(Player player, GachaEffect effect) {
        GachaDataManager.GachaPlayerData playerData = plugin.getGachaDataManager().getPlayerData(player);
        switch (effect.getType()) {
            case ARROW_TRAIL:
                playerData.setArrowTrailEffect(effect);
                break;
            case DEATH:
                playerData.setDeathEffect(effect);
                break;
            case AURA:
                playerData.setAuraEffect(effect);
                break;
        }
        player.sendMessage(plugin.getGachaConfigManager().getMessage("effect-equipped")
                .replaceText(builder -> builder.match("%effect%").replacement(effect.getId())));
    }
}

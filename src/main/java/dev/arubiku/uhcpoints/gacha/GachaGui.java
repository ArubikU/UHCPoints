package dev.arubiku.uhcpoints.gacha;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import dev.arubiku.uhcpoints.gacha.GachaDataManager.GachaPlayerData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;

public class GachaGui {
    private final GachaponManager plugin;

    public GachaGui(GachaponManager plugin) {
        this.plugin = plugin;
    }

    public void openMainMenu(Player player) {
        GachaConfigManager.GUIConfig guiConfig = plugin.getGachaConfigManager().getGUIConfig("main_menu");
        Inventory inventory = createInventory(guiConfig, new MainMenuHolder());

        for (Map.Entry<Integer, GachaConfigManager.GUIItem> entry : guiConfig.getItems().entrySet()) {
            inventory.setItem(entry.getKey(), createGuiItem(entry.getValue()));
        }

        player.openInventory(inventory);
    }

    public void openEquipMenu(Player player) {
        GachaConfigManager.GUIConfig guiConfig = plugin.getGachaConfigManager().getGUIConfig("equip_menu");
        Inventory inventory = createInventory(guiConfig, new EquipMenuHolder());

        GachaPlayerData playerData = plugin.getGachaDataManager().getPlayerData(player);
        List<String> unlockedEffects = playerData.getUnlockedEffects();

        for (int i = 0; i < unlockedEffects.size() && i < guiConfig.getSize(); i++) {
            GachaEffect effect = GachaEffect.getById(unlockedEffects.get(i));
            ItemStack item = createEffectItem(effect);
            inventory.setItem(i, item);
        }

        player.openInventory(inventory);
    }

    private Inventory createInventory(GachaConfigManager.GUIConfig guiConfig, InventoryHolder holder) {
        return Bukkit.createInventory(holder, guiConfig.getSize(), Component.text(guiConfig.getTitle()));
    }

    public ItemStack createGuiItem(GachaConfigManager.GUIItem guiItem) {
        Material material = Material.valueOf(guiItem.getMaterial().toUpperCase());
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text(guiItem.getName()).decoration(TextDecoration.ITALIC, false));
        List<Component> lore = new ArrayList<>();
        for (String loreLine : guiItem.getLore()) {
            lore.add(Component.text(loreLine).decoration(TextDecoration.ITALIC, false));
        }
        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createEffectItem(GachaEffect effect) {
        Material material;
        switch (effect.getType()) {
            case ARROW_TRAIL:
                material = Material.ARROW;
                break;
            case DEATH:
                material = Material.SKELETON_SKULL;
                break;
            case AURA:
                material = Material.NETHER_STAR;
                break;
            default:
                material = Material.BARRIER;
        }

        GachaConfigManager.PrizeTranslation translation = plugin.getGachaConfigManager()
                .getPrizeTranslation(effect.getId());
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(translation.getName().decoration(TextDecoration.ITALIC, false));
        meta.lore(List.of(translation.getLore().decoration(TextDecoration.ITALIC, false)));
        item.setItemMeta(meta);
        return item;
    }

    public static class MainMenuHolder implements InventoryHolder {
        @Override
        public Inventory getInventory() {
            return null;
        }
    }

    public static class EquipMenuHolder implements InventoryHolder {
        @Override
        public Inventory getInventory() {
            return null;
        }
    }
}

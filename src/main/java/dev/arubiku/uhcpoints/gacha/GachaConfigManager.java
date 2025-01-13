package dev.arubiku.uhcpoints.gacha;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class GachaConfigManager {
    private final GachaponManager plug;
    private FileConfiguration config;
    private Map<String, String> messages;
    private Map<String, PrizeTranslation> prizeTranslations;
    private Map<String, GUIConfig> guiConfigs;

    public GachaConfigManager(GachaponManager plug) {
        this.plug = plug;
        this.messages = new HashMap<>();
        this.prizeTranslations = new HashMap<>();
        this.guiConfigs = new HashMap<>();
    }

    public void loadConfig() {
        File configFile = new File(plug.getPlugin().getDataFolder(), "gacha.yml");
        if (!configFile.exists()) {
            plug.getPlugin().saveResource("gacha.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(configFile);
        loadMessages();
        loadPrizeTranslations();
        loadGUIConfigs();
    }

    private void loadMessages() {
        messages.clear();
        ConfigurationSection messagesSection = config.getConfigurationSection("messages");
        if (messagesSection != null) {
            for (String key : messagesSection.getKeys(false)) {
                messages.put(key, messagesSection.getString(key));
            }
        }
    }

    private void loadPrizeTranslations() {
        prizeTranslations.clear();
        ConfigurationSection translationSection = config.getConfigurationSection("translation");
        if (translationSection != null) {
            for (String id : translationSection.getKeys(false)) {
                ConfigurationSection prizeSection = translationSection.getConfigurationSection(id);
                if (prizeSection != null) {
                    String name = prizeSection.getString("name");
                    String lore = prizeSection.getString("lore");
                    prizeTranslations.put(id, new PrizeTranslation(name, lore));
                }
            }
        }
    }

    private void loadGUIConfigs() {
        guiConfigs.clear();
        ConfigurationSection guiSection = config.getConfigurationSection("guis");
        if (guiSection != null) {
            for (String guiKey : guiSection.getKeys(false)) {
                ConfigurationSection specificGuiSection = guiSection.getConfigurationSection(guiKey);
                if (specificGuiSection != null) {
                    String title = specificGuiSection.getString("title");
                    int size = specificGuiSection.getInt("size");
                    Map<Integer, GUIItem> items = new HashMap<>();
                    ConfigurationSection itemsSection = specificGuiSection.getConfigurationSection("items");
                    if (itemsSection != null) {
                        for (String itemKey : itemsSection.getKeys(false)) {
                            ConfigurationSection itemSection = itemsSection.getConfigurationSection(itemKey);
                            if (itemSection != null) {
                                int slot = itemSection.getInt("slot");
                                String material = itemSection.getString("material");
                                String itemName = itemSection.getString("name");
                                List<String> lore = itemSection.getStringList("lore");
                                items.put(slot, new GUIItem(material, itemName, lore));
                            }
                        }
                    }
                    guiConfigs.put(guiKey, new GUIConfig(title, size, items));
                }
            }
        }
    }

    public Component getMessage(String key) {
        String message = messages.getOrDefault(key, "Message not found: " + key);
        return MiniMessage.miniMessage().deserialize(message);
    }

    public PrizeTranslation getPrizeTranslation(String id) {
        return prizeTranslations.getOrDefault(id, new PrizeTranslation("Unknown Prize", "No description available"));
    }

    public GUIConfig getGUIConfig(String guiKey) {
        return guiConfigs.getOrDefault(guiKey, new GUIConfig("Default Title", 27, new HashMap<>()));
    }

    public int getCost() {
        return config.getInt("cost", 100);
    }

    public List<PrizeConfig> getPrizes() {
        List<PrizeConfig> prizes = new ArrayList<>();
        ConfigurationSection prizesSection = config.getConfigurationSection("prizes");
        if (prizesSection != null) {
            for (String key : prizesSection.getKeys(false)) {
                ConfigurationSection prizeSection = prizesSection.getConfigurationSection(key);
                if (prizeSection != null) {
                    String id = prizeSection.getString("id");
                    String type = prizeSection.getString("type");
                    String rarity = prizeSection.getString("rarity");
                    int chance = prizeSection.getInt("chance");
                    prizes.add(new PrizeConfig(id, type, rarity, chance));
                }
            }
        }
        return prizes;
    }

    public static class PrizeTranslation {
        private final Component name;
        private final Component lore;

        public PrizeTranslation(String name, String lore) {
            this.name = MiniMessage.miniMessage().deserialize(name);
            this.lore = MiniMessage.miniMessage().deserialize(lore);
        }

        public Component getName() {
            return name;
        }

        public Component getLore() {
            return lore;
        }
    }

    public static class GUIConfig {
        private final String title;
        private final int size;
        private final Map<Integer, GUIItem> items;

        public GUIConfig(String title, int size, Map<Integer, GUIItem> items) {
            this.title = title;
            this.size = size;
            this.items = items;
        }

        public String getTitle() {
            return title;
        }

        public int getSize() {
            return size;
        }

        public Map<Integer, GUIItem> getItems() {
            return items;
        }
    }

    public static class GUIItem {
        private final String material;
        private final String name;
        private final List<String> lore;

        public GUIItem(String material, String name, List<String> lore) {
            this.material = material;
            this.name = name;
            this.lore = lore;
        }

        public String getMaterial() {
            return material;
        }

        public String getName() {
            return name;
        }

        public List<String> getLore() {
            return lore;
        }
    }

    public static class PrizeConfig {
        private final String id;
        private final String type;
        private final String rarity;
        private final int chance;

        public PrizeConfig(String id, String type, String rarity, int chance) {
            this.id = id;
            this.type = type;
            this.rarity = rarity;
            this.chance = chance;
        }

        public String getId() {
            return id;
        }

        public String getType() {
            return type;
        }

        public String getRarity() {
            return rarity;
        }

        public int getChance() {
            return chance;
        }
    }
}

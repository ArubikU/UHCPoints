package dev.arubiku.uhcpoints.gacha;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.entity.Player;

import dev.arubiku.uhcpoints.gacha.GachaConfigManager.PrizeConfig;

public class GachaManager {
    private final GachaponManager plugin;
    private final Random random;

    public GachaManager(GachaponManager plugin) {
        this.plugin = plugin;
        this.random = new Random();
    }

    public boolean cantSpin(Player player) {
        List<GachaConfigManager.PrizeConfig> prizes = plugin.getGachaConfigManager().getPrizes();
        List<GachaEffect> prices = new ArrayList<>();
        for (PrizeConfig conf : prizes) {
            if (!plugin.getGachaDataManager().getPlayerData(player).getUnlockedEffects().contains(conf.getId())) {
                for (int i = 0; i < conf.getChance(); i++) {
                    prices.add(GachaEffect.getById(conf.getId()));
                }
            }
        }
        return prices.isEmpty();

    }

    public GachaEffect spinGacha(Player player) {
        List<GachaConfigManager.PrizeConfig> prizes = plugin.getGachaConfigManager().getPrizes();
        List<GachaEffect> prices = new ArrayList<>();
        for (PrizeConfig conf : prizes) {
            if (!plugin.getGachaDataManager().getPlayerData(player).getUnlockedEffects().contains(conf.getId())) {
                for (int i = 0; i < conf.getChance(); i++) {
                    prices.add(GachaEffect.getById(conf.getId()));
                }
            }
        }
        return prices.get(random.nextInt(prices.size()));
    }
}

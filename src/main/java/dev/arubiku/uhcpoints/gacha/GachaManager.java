package dev.arubiku.uhcpoints.gacha;

import java.util.List;
import java.util.Random;

public class GachaManager {
    private final GachaponManager plugin;
    private final Random random;

    public GachaManager(GachaponManager plugin) {
        this.plugin = plugin;
        this.random = new Random();
    }

    public GachaEffect spinGacha() {
        List<GachaConfigManager.PrizeConfig> prizes = plugin.getGachaConfigManager().getPrizes();
        int totalWeight = prizes.stream().mapToInt(GachaConfigManager.PrizeConfig::getChance).sum();
        int randomNumber = random.nextInt(totalWeight);

        int currentWeight = 0;
        for (GachaConfigManager.PrizeConfig prize : prizes) {
            currentWeight += prize.getChance();
            if (randomNumber < currentWeight) {
                return GachaEffect.getById(prize.getId());
            }
        }

        // Fallback to first prize if something goes wrong
        return GachaEffect.getById(prizes.get(0).getId());
    }
}

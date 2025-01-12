package dev.arubiku.uhcpoints.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import dev.arubiku.uhcpoints.UHCPoints;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class ChatListener implements Listener {
    private final UHCPoints plugin;

    public ChatListener(UHCPoints plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        String rank = plugin.getEffectManager().getPlayerRank(player);
        String prefix = plugin.getConfig().getString("ranks." + rank + ".prefix", "");
        String suffix = plugin.getConfig().getString("ranks." + rank + ".suffix", "");

        Component prefixComponent = MiniMessage.miniMessage().deserialize(prefix);
        Component suffixComponent = MiniMessage.miniMessage().deserialize(suffix);
        Component playerName = Component.text(player.getName(), NamedTextColor.WHITE);
        Component message = event.message();

        Component formattedMessage = prefixComponent
                .append(playerName)
                .append(suffixComponent)
                .append(Component.text(": "))
                .append(message);

        event.message(formattedMessage);
    }
}

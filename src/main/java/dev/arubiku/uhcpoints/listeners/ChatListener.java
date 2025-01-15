package dev.arubiku.uhcpoints.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import dev.arubiku.uhcpoints.UHCPoints;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.md_5.bungee.api.ChatColor;

public class ChatListener implements Listener {
    private final UHCPoints plugin;

    public ChatListener(UHCPoints plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        String rank = plugin.getEffectManager().getPlayerRank(player);

        String prefixprefix = plugin.getConfig().getString("options.prefixprefix",
                "<hover:show_text:'Click to teleport'><click:run_command:'/teleport %player%'");
        String prefixsuffix = plugin.getConfig().getString("options.prefixsuffix", "</click></hover>");
        prefixprefix = prefixprefix.replace("%player%", ChatColor.stripColor(player.getName()));
        String prefix = prefixprefix
                + plugin.getConfig().getString("ranks." + rank + ".prefix", "")
                + ChatColor.stripColor(player.getName()) + prefixsuffix;
        String suffix = plugin.getConfig().getString("ranks." + rank + ".suffix", "");

        Component prefixComponent = MiniMessage.miniMessage().deserialize(prefix);
        Component suffixComponent = MiniMessage.miniMessage().deserialize(suffix);
        Component message = event.message();
        Component chatJoin = Component.text().content(" : ").build();
        event.setCancelled(true);
        Component end = Component.empty().append(prefixComponent).append(suffixComponent)
                .append(chatJoin).append(message);

        if (plugin.getConfig().getBoolean("options.dead-can-talks-live", false)) {
            if (UHCPointsListener.deadPlayers.contains(player.getName())) {
                chatJoin = Component.text().content(" : <gray><italic>").build();
                Component endforspectator = Component.empty().append(prefixComponent).append(suffixComponent)
                        .append(chatJoin).append(message);
                for (Player pl : Bukkit.getOnlinePlayers()) {
                    if (pl.hasPermission("uhcpoints.force-chat")) {
                        pl.sendMessage(
                                endforspectator.append(MiniMessage.miniMessage().deserialize("<gray><italic>[spec]")));
                    }
                    if (!UHCPointsListener.deadPlayers.contains(pl.getName()))
                        continue;
                    if (pl.getUniqueId() == event.getPlayer().getUniqueId())
                        return;
                    pl.sendMessage(endforspectator);
                }
            } else {

                for (Player pl : Bukkit.getOnlinePlayers()) {
                    pl.sendMessage(end);
                }
            }
        } else {

            for (Player pl : Bukkit.getOnlinePlayers()) {
                pl.sendMessage(end);
            }
        }
    }
}

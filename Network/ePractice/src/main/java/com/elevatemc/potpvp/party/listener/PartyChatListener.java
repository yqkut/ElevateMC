package com.elevatemc.potpvp.party.listener;

import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.party.Party;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class PartyChatListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        if (!event.getMessage().startsWith("@")) {
            return;
        }

        event.setCancelled(true);

        Player player = event.getPlayer();
        String message = event.getMessage().substring(1).trim();
        Party party = PotPvPSI.getInstance().getPartyHandler().getParty(player);

        if (party == null) {
            player.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You have to be in a party to use this chat!");
            return;
        }

        ChatColor prefixColor = party.isLeader(player.getUniqueId()) ? ChatColor.UNDERLINE : ChatColor.AQUA;
        party.message(ChatColor.DARK_AQUA + "✭ " + ChatColor.AQUA + prefixColor + player.getName() + ": " + ChatColor.WHITE + message);

        PotPvPSI.getInstance().getLogger().info("[Party] " + player.getName() + ": " + message);
    }

}
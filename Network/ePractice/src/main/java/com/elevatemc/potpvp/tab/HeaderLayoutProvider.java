package com.elevatemc.potpvp.tab;


import com.elevatemc.potpvp.PotPvPSI;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.function.BiConsumer;

final class HeaderLayoutProvider implements BiConsumer<Player, PotPvPLayoutProvider.TabLayout> {

    @Override
    public void accept(Player player, PotPvPLayoutProvider.TabLayout tabLayout) {
        header: {
            tabLayout.put(1, 0, ChatColor.DARK_AQUA.toString() + ChatColor.BOLD + "Practice");
        }

        status: {
            tabLayout.put(0, 1, ChatColor.GRAY + "Online: " + PotPvPSI.getInstance().getOnlineCount());
//            tabLayout.put(1, 1, ChatColor.GRAY + "Your Connection", PotPvPLayoutProvider.getPingOrDefault(player.getUniqueId()));
            tabLayout.put(2, 1, ChatColor.GRAY + "Fighting: " + PotPvPSI.getInstance().getFightsCount());
        }
    }

}

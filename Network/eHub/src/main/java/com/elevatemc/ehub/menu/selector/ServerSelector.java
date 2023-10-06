package com.elevatemc.ehub.menu.selector;

import com.elevatemc.elib.menu.Button;
import com.elevatemc.elib.menu.Menu;
import com.elevatemc.elib.util.Pair;
import com.google.common.collect.ImmutableList;
import dev.apposed.prime.spigot.module.server.scoreboard.PrimeScoreboardStyle;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class ServerSelector extends Menu {
    public ServerSelector() {
        setAutoUpdate(true);
        setPlaceholder(true);
    }

    @Override
    public String getTitle(Player player) {
        final Pair<ChatColor, ChatColor> style = PrimeScoreboardStyle.getStyle(player);
        return style.getKey() + "Select a Game";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        buttons.put(13, new ServerButton(Material.IRON_SWORD, "Practice NA", "Practice", "North America",
                ImmutableList.of(
                    ChatColor.GRAY + "Our official practice playing experience",
                    ChatColor.GRAY + "with multiple features"
                )
        ));
        return buttons;
    }

    @Override
    public int size(Player player) {
        return 9*3;
    }
}

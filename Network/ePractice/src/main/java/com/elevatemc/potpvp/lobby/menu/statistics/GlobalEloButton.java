package com.elevatemc.potpvp.lobby.menu.statistics;

import com.google.common.collect.Lists;
import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.elo.EloHandler;
import com.elevatemc.elib.menu.Button;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map.Entry;

public class GlobalEloButton extends Button {
    private Player target;

    public GlobalEloButton(Player target) {
        this.target = target;
    }

    private static EloHandler eloHandler = PotPvPSI.getInstance().getEloHandler();

    @Override
    public String getName(Player player) {
        return ChatColor.AQUA + ChatColor.BOLD.toString() + "Global ❘ Top 10";
    }

    @Override
    public List<String> getDescription(Player player) {
        List<String> description = Lists.newArrayList();

        description.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "");

        int counter = 1;

        for (Entry<String, Integer> entry : eloHandler.topElo(null).entrySet()) {
            String color = String.valueOf(ChatColor.AQUA);
            description.add(color + "#" + counter + ChatColor.AQUA + ": " + entry.getKey() + ChatColor.GRAY + " - " + ChatColor.AQUA + entry.getValue());

            counter++;
        }


        return description;
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.NETHER_STAR;
    }
}

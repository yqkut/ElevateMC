package com.elevatemc.potpvp.lobby.menu.statistics;

import com.google.common.collect.Lists;
import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.elo.EloHandler;
import com.elevatemc.potpvp.gamemode.GameMode;
import com.elevatemc.elib.menu.Button;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map.Entry;

public class KitButton extends Button {

    private static EloHandler eloHandler = PotPvPSI.getInstance().getEloHandler();

    private GameMode gameMode;
    private Player target;

    public KitButton(GameMode gameMode, Player target) {
        this.target = target;
        this.gameMode = gameMode;
    }

    @Override
    public String getName(Player player) {
        return ChatColor.AQUA + ChatColor.BOLD.toString() + gameMode.getName() +  " ❘ Top 10";
    }

    @Override
    public List<String> getDescription(Player player) {
        List<String> description = Lists.newArrayList();

        description.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "");

        int counter = 1;

        for (Entry<String, Integer> entry : eloHandler.topElo(gameMode).entrySet()) {
            String color = ChatColor.AQUA.toString();
            description.add(color + "#" + counter + ChatColor.AQUA + ": " + entry.getKey() + ChatColor.GRAY + ChatColor.GRAY + " - " + ChatColor.WHITE + entry.getValue());

            counter++;
        }


        return description;
    }

    @Override
    public Material getMaterial(Player player) {
        return gameMode.getIcon().getItemType();
    }

    @Override
    public byte getDamageValue(Player player) {
        return gameMode.getIcon().getData();
    }
}

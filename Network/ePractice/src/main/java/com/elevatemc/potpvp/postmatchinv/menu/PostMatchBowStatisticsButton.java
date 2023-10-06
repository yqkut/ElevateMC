package com.elevatemc.potpvp.postmatchinv.menu;

import com.elevatemc.elib.util.UUIDUtils;
import com.google.common.collect.ImmutableList;
import com.elevatemc.elib.menu.Button;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

final class PostMatchBowStatisticsButton extends Button {
    private final UUID player;
    private final int tags;


    PostMatchBowStatisticsButton(UUID player, int tags) {
        this.player = player;
        this.tags = tags;
    }

    @Override
    public String getName(Player player) {
        return ChatColor.GREEN + "Bow Statistics";
    }

    @Override
    public List<String> getDescription(Player player) {
        return ImmutableList.of(
                ChatColor.YELLOW + UUIDUtils.name(this.player) + " had " + this.tags + " tag" + (tags == 1 ? "" : "s") + " in total."
        );
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.BOW;
    }

    @Override
    public int getAmount(Player player) {
        return 1;
    }

}
package com.elevatemc.potpvp.arena.menu.select.teamfight;

import com.elevatemc.elib.menu.Button;
import com.elevatemc.elib.util.Callback;
import com.elevatemc.potpvp.arena.ArenaTeamfightCategory;
import com.google.common.collect.ImmutableList;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.List;

@AllArgsConstructor
public class ArenaTeamfightCategoryButton extends Button {

    private ArenaTeamfightCategory arenaTeamfightCategory;
    private Callback<ArenaTeamfightCategory> callback;

    @Override
    public String getName(Player player) {
        return ChatColor.DARK_AQUA + arenaTeamfightCategory.getName();
    }

    @Override
    public List<String> getDescription(Player player) {
        return ImmutableList.of(
                ChatColor.DARK_AQUA + "❘ " + ChatColor.WHITE + arenaTeamfightCategory.getDescription()
        );
    }

    @Override
    public Material getMaterial(Player player) {
        return arenaTeamfightCategory.getIcon();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        callback.callback(arenaTeamfightCategory);
    }
}

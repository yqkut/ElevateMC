package com.elevatemc.potpvp.arena.menu.select;

import com.google.common.collect.ImmutableList;
import lombok.AllArgsConstructor;
import com.elevatemc.potpvp.arena.ArenaSchematic;
import com.elevatemc.elib.menu.Button;
import com.elevatemc.elib.util.Callback;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.List;

@AllArgsConstructor
public class ArenaButton extends Button {

    private ArenaSchematic arenaSchematic;
    private final Callback<String> callback;

    @Override
    public String getName(Player player) {
        return ChatColor.GREEN + arenaSchematic.getDisplayName();
    }
    
    @Override
    public List<String> getDescription(Player player) {
        return ImmutableList.of(
                ChatColor.GRAY + "Click here to select this map"
        );
    }

    @Override
    public Material getMaterial(Player player) {
        return arenaSchematic.getIcon();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        callback.callback(arenaSchematic.getName());
    }
}

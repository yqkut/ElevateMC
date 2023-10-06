package com.elevatemc.potpvp.arena.menu.manageschematic;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.arena.ArenaHandler;
import com.elevatemc.potpvp.arena.ArenaSchematic;
import com.elevatemc.elib.menu.Button;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.List;

final class RemoveCopiesButton extends Button {

    private final ArenaSchematic schematic;

    RemoveCopiesButton(ArenaSchematic schematic) {
        this.schematic = Preconditions.checkNotNull(schematic, "schematic");
    }

    @Override
    public String getName(Player player) {
        return ChatColor.RED + "Remove copies of " + schematic.getName() + "";
    }

    @Override
    public List<String> getDescription(Player player) {
        return ImmutableList.of(
            "",
            ChatColor.RED.toString() + ChatColor.BOLD + "CLICK " + ChatColor.RED + "to remove 1 copy",
            ChatColor.RED.toString() + ChatColor.BOLD + "SHIFT-CLICK " + ChatColor.RED + "to remove 10 copies",
            "",
            ChatColor.AQUA + "Scale directly to a desired quantity",
            ChatColor.AQUA + "with /arena scale " + schematic.getName() + " <count>"
        );
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.REDSTONE_BLOCK;
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        ArenaHandler arenaHandler = PotPvPSI.getInstance().getArenaHandler();
        int existing = arenaHandler.countArenas(schematic);
        int remove = clickType.isShiftClick() ? 10 : 1;
        int desired = Math.max(existing - remove, 0);

        if (arenaHandler.getGrid().isBusy()) {
            player.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "Grid is busy.");
            return;
        }

        player.sendMessage(ChatColor.GREEN + "Starting...");

        arenaHandler.getGrid().scaleCopies(schematic, desired, () -> {
            player.sendMessage(ChatColor.GREEN + "Scaled " + schematic.getName() + " to " + desired + ".");
        });
    }

}
package com.elevatemc.potpvp.arena.menu.manageschematics;

import com.google.common.base.Preconditions;
import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.arena.Arena;
import com.elevatemc.potpvp.arena.ArenaHandler;
import com.elevatemc.potpvp.arena.ArenaSchematic;
import com.elevatemc.potpvp.arena.menu.manageschematic.ManageSchematicMenu;
import com.elevatemc.elib.menu.Button;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;

final class ManageSchematicButton extends Button {

    private final ArenaSchematic schematic;

    ManageSchematicButton(ArenaSchematic schematic) {
        this.schematic = Preconditions.checkNotNull(schematic, "schematic");
    }

    @Override
    public String getName(Player player) {
        return ChatColor.RED + schematic.getName();
    }

    @Override
    public List<String> getDescription(Player player) {
        ArenaHandler arenaHandler = PotPvPSI.getInstance().getArenaHandler();
        int totalCopies = 0;
        int inUseCopies = 0;

        for (Arena arena : arenaHandler.getArenas(schematic)) {
            totalCopies++;

            if (arena.isInUse()) {
                inUseCopies++;
            }
        }

        List<String> description = new ArrayList<>();

        description.add(ChatColor.DARK_AQUA + "Display Name: "+ ChatColor.WHITE + schematic.getDisplayName());
        description.add(ChatColor.DARK_AQUA + "Enabled: " + ChatColor.WHITE + (schematic.isEnabled() ? ChatColor.GREEN + "Yes" : ChatColor.RED + "No"));
        description.add(ChatColor.DARK_AQUA + "Copies: " + ChatColor.WHITE + inUseCopies + "/" + totalCopies);

        return description;
    }

    @Override
    public int getAmount(Player player) {
        ArenaHandler arenaHandler = PotPvPSI.getInstance().getArenaHandler();
        return arenaHandler.getArenas(schematic).size();
    }

    @Override
    public Material getMaterial(Player player) {
        return schematic.getIcon();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        player.closeInventory();
        new ManageSchematicMenu(schematic).openMenu(player);
    }

}
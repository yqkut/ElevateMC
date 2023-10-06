package com.elevatemc.potpvp.kit.menu.editkit;

import com.elevatemc.potpvp.gamemode.kit.GameModeKit;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.elevatemc.potpvp.kit.menu.kits.KitsMenu;
import com.elevatemc.potpvp.gamemode.GameMode;
import com.elevatemc.potpvp.util.InventoryUtils;
import com.elevatemc.elib.menu.Button;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.List;

final class CancelKitEditButton extends Button {

    private final GameModeKit gameModeKit;

    CancelKitEditButton(GameModeKit gameModeKit) {
        this.gameModeKit = Preconditions.checkNotNull(gameModeKit, "gameMode");
    }

    @Override
    public String getName(Player player) {
        return ChatColor.DARK_RED.toString() + ChatColor.BOLD + "Cancel";
    }

    @Override
    public List<String> getDescription(Player player) {
        return ImmutableList.of(
            ChatColor.RED + "Click this to abort editing your kit,",
            ChatColor.RED + "and return to the kit menu."
        );
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.STAINED_CLAY;
    }

    @Override
    public byte getDamageValue(Player player) {
        return DyeColor.RED.getWoolData();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        player.closeInventory();
        InventoryUtils.resetInventoryDelayed(player);

        new KitsMenu(gameModeKit).openMenu(player);
    }

}
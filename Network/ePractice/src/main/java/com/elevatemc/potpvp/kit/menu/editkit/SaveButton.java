package com.elevatemc.potpvp.kit.menu.editkit;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.kit.Kit;
import com.elevatemc.potpvp.kit.menu.kits.KitsMenu;
import com.elevatemc.potpvp.util.InventoryUtils;
import com.elevatemc.potpvp.util.ItemUtils;
import com.elevatemc.elib.menu.Button;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;

final class SaveButton extends Button {

    private final Kit kit;

    SaveButton(Kit kit) {
        this.kit = Preconditions.checkNotNull(kit, "kit");
    }

    @Override
    public String getName(Player player) {
        return ChatColor.DARK_GREEN.toString() + ChatColor.BOLD + "Save";
    }

    @Override
    public List<String> getDescription(Player player) {
        return ImmutableList.of(
            ChatColor.GREEN + "Click this to save your kit."
        );
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.STAINED_CLAY;
    }

    @Override
    public byte getDamageValue(Player player) {
        return DyeColor.LIME.getWoolData();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        kit.setInventoryContents(player.getInventory().getContents());
        PotPvPSI.getInstance().getKitHandler().saveKitsAsync(player);

        player.setItemOnCursor(new ItemStack(Material.AIR));

        player.closeInventory();
        InventoryUtils.resetInventoryDelayed(player);
        new KitsMenu(kit.getType()).openMenu(player);
    }

}
package com.elevatemc.potpvp.hctranked.game;

import com.elevatemc.elib.util.ItemUtils;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;


import static org.bukkit.ChatColor.*;

public class RankedGameItems {
    public static final ItemStack GAME_INFO = new ItemStack(Material.WATCH);
    public static final ItemStack LEAVE_GAME = new ItemStack(Material.INK_SACK, 1, DyeColor.RED.getDyeData());
    public static final ItemStack ASSIGN_CLASSES = new ItemStack(Material.MAGMA_CREAM);

    public static final ItemStack NOT_READY = new ItemStack(Material.INK_SACK, 1, DyeColor.GRAY.getDyeData());
    public static final ItemStack READY = new ItemStack(Material.INK_SACK, 1, DyeColor.LIME.getDyeData());

    static {
        ItemUtils.setDisplayName(GAME_INFO, GRAY + "• " + RED + "Game Info" + GRAY + " •");
        ItemUtils.setDisplayName(LEAVE_GAME, GRAY + "• " + RED + "Leave Game" + GRAY + " •");
        ItemUtils.setDisplayName(ASSIGN_CLASSES, GRAY + "• " + AQUA + "Ranked Kits" + GRAY + " •");

        ItemUtils.setDisplayName(NOT_READY, GRAY + "• " + RED + "Not Ready" + GRAY + " •");
        ItemUtils.setDisplayName(READY, GRAY + "• " + GREEN + "Ready" + GRAY + " •");
    }
}

package com.elevatemc.potpvp.match;

import lombok.experimental.UtilityClass;
import com.elevatemc.elib.util.ItemUtils;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import static org.bukkit.ChatColor.GRAY;
import static org.bukkit.ChatColor.RED;
import static org.bukkit.ChatColor.YELLOW;

@UtilityClass
public final class SpectatorItems {

    public static final ItemStack SHOW_SPECTATORS_ITEM = new ItemStack(Material.INK_SACK, 1, DyeColor.GRAY.getDyeData());
    public static final ItemStack HIDE_SPECTATORS_ITEM = new ItemStack(Material.INK_SACK, 1, DyeColor.LIME.getDyeData());

    public static final ItemStack VIEW_INVENTORY_ITEM = new ItemStack(Material.BOOK);

    // these items both do the same thing but we change the name if
    // clicking the item will reuslt in the player being removed
    // from their party. both serve the function of returning a player
    // to the lobby.
    // https://github.com/ElevateOrb/PotPvP-SI/issues/37
    public static final ItemStack RETURN_TO_LOBBY_ITEM = new ItemStack(Material.INK_SACK, 1, DyeColor.RED.getDyeData());
    public static final ItemStack LEAVE_PARTY_ITEM = new ItemStack(Material.INK_SACK, 1, DyeColor.RED.getDyeData());

    static {
        ItemUtils.setDisplayName(SHOW_SPECTATORS_ITEM, GRAY + "• " + YELLOW + "Show spectators" + GRAY + " •");
        ItemUtils.setDisplayName(HIDE_SPECTATORS_ITEM, GRAY + "• " + YELLOW + "Hide spectators" + GRAY + " •");

        ItemUtils.setDisplayName(VIEW_INVENTORY_ITEM, GRAY + "• " + YELLOW + "View player inventory" + GRAY + " •");

        ItemUtils.setDisplayName(RETURN_TO_LOBBY_ITEM, GRAY + "• " + YELLOW + "Return to lobby" + GRAY + " •");
        ItemUtils.setDisplayName(LEAVE_PARTY_ITEM, GRAY + "• " + RED + "Leave Party" + GRAY + " •" + GRAY + " •");
    }

}
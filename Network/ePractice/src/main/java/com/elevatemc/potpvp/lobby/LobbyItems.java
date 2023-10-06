package com.elevatemc.potpvp.lobby;

import lombok.experimental.UtilityClass;
import com.elevatemc.elib.util.ItemUtils;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import static org.bukkit.ChatColor.*;

@UtilityClass
public final class LobbyItems {

    public static final ItemStack SPECTATE_RANDOM_ITEM = new ItemStack(Material.COMPASS);
    public static final ItemStack SPECTATE_MENU_ITEM = new ItemStack(Material.PAPER);
    public static final ItemStack UNFOLLOW_ITEM = new ItemStack(Material.INK_SACK, 1, DyeColor.RED.getDyeData());
    public static final ItemStack PLAYER_STATISTICS = new ItemStack(Material.EMERALD, 1);
    public static final ItemStack CREATE_TEAM = new ItemStack(Material.NAME_TAG, 1);
    public static final ItemStack HOST_EVENTS = new ItemStack(Material.BEACON, 1);

    static {
        ItemUtils.setDisplayName(SPECTATE_RANDOM_ITEM, GRAY + "• " + AQUA + "Spectate Random Match" + GRAY + " •");
        ItemUtils.setDisplayName(SPECTATE_MENU_ITEM, GRAY + "• " + AQUA + "Spectator Menu" + GRAY + " •");
        ItemUtils.setDisplayName(UNFOLLOW_ITEM, GRAY + "• " + RED + BOLD + "Stop Following" + GRAY + " •");
        ItemUtils.setDisplayName(PLAYER_STATISTICS, GRAY + "• " + AQUA + "Statistics" + GRAY + " •");
        ItemUtils.setDisplayName(CREATE_TEAM, GRAY + "• " + AQUA + "Create Party" + GRAY + " •");
        ItemUtils.setDisplayName(HOST_EVENTS, GRAY + "• " + AQUA + "Host Events" + GRAY + " •");
    }
}
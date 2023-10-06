package com.elevatemc.potpvp.match.rematch;

import lombok.experimental.UtilityClass;
import com.elevatemc.elib.util.ItemUtils;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;


import static org.bukkit.ChatColor.*;

@UtilityClass
public final class RematchItems {

    public static final ItemStack REQUEST_REMATCH_ITEM = new ItemStack(Material.BLAZE_POWDER);
    public static final ItemStack SENT_REMATCH_ITEM = new ItemStack(Material.INK_SACK, 1, DyeColor.RED.getDyeData());
    public static final ItemStack ACCEPT_REMATCH_ITEM = new ItemStack(Material.EMERALD);

    static {
        ItemUtils.setDisplayName(REQUEST_REMATCH_ITEM, GRAY + "• " + RED + "Request Rematch" + GRAY + " •");
        ItemUtils.setDisplayName(SENT_REMATCH_ITEM, GRAY + "• " + GREEN + "Sent Rematch" + GRAY + " •");
        ItemUtils.setDisplayName(ACCEPT_REMATCH_ITEM, GRAY + "• " + GREEN + "Accept Rematch" + GRAY + " •");
    }

}
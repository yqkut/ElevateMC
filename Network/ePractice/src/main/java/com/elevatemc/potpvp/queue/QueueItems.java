package com.elevatemc.potpvp.queue;

import lombok.experimental.UtilityClass;
import com.elevatemc.elib.util.ItemUtils;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;


import static org.bukkit.ChatColor.*;

@UtilityClass
public final class QueueItems {

    public static final ItemStack JOIN_SOLO_UNRANKED_QUEUE_ITEM = new ItemStack(Material.IRON_SWORD);
    public static final ItemStack LEAVE_SOLO_UNRANKED_QUEUE_ITEM = new ItemStack(Material.INK_SACK, 1, DyeColor.RED.getDyeData());

    public static final ItemStack JOIN_SOLO_RANKED_QUEUE_ITEM = new ItemStack(Material.DIAMOND_SWORD);
    public static final ItemStack LEAVE_SOLO_RANKED_QUEUE_ITEM = new ItemStack(Material.INK_SACK, 1, DyeColor.RED.getDyeData());

    public static final ItemStack JOIN_PARTY_UNRANKED_QUEUE_ITEM = new ItemStack(Material.IRON_SWORD);
    public static final ItemStack LEAVE_PARTY_UNRANKED_QUEUE_ITEM = new ItemStack(Material.INK_SACK, 1, DyeColor.RED.getDyeData());

    public static final ItemStack JOIN_PARTY_RANKED_QUEUE_ITEM = new ItemStack(Material.DIAMOND_SWORD);
    public static final ItemStack LEAVE_PARTY_RANKED_QUEUE_ITEM = new ItemStack(Material.INK_SACK, 1, DyeColor.RED.getDyeData());

    static {
        ItemUtils.setDisplayName(JOIN_SOLO_UNRANKED_QUEUE_ITEM,  GRAY + "• " + AQUA + "Casual Queue" + GRAY + " •");
        ItemUtils.setDisplayName(LEAVE_SOLO_UNRANKED_QUEUE_ITEM, GRAY + "• " + RED + "Leave Queue" + GRAY + " •");

        ItemUtils.setDisplayName(JOIN_SOLO_RANKED_QUEUE_ITEM, GRAY + "• " + AQUA + "Competitive Queue" + GRAY + " •");
        ItemUtils.setDisplayName(LEAVE_SOLO_RANKED_QUEUE_ITEM, GRAY + "• " + RED + "Leave Queue" + GRAY + " •");

        ItemUtils.setDisplayName(JOIN_PARTY_UNRANKED_QUEUE_ITEM, GRAY + "• " + AQUA + "Casual 2v2 Queue" + GRAY + " •");
        ItemUtils.setDisplayName(LEAVE_PARTY_UNRANKED_QUEUE_ITEM, GRAY + "• " + RED + "Leave 2v2 Queue" + GRAY + " •");

        ItemUtils.setDisplayName(JOIN_PARTY_RANKED_QUEUE_ITEM, GRAY + "• " + AQUA + "Ranked 2v2 Queue" + GRAY + " •");
        ItemUtils.setDisplayName(LEAVE_PARTY_RANKED_QUEUE_ITEM, GRAY + "• " + RED + "Leave 2v2 Queue" + GRAY + " •");
    }

}
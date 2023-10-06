package com.elevatemc.potpvp.party;

import lombok.experimental.UtilityClass;
import com.elevatemc.elib.util.ItemUtils;
import com.elevatemc.elib.util.UUIDUtils;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import static org.bukkit.ChatColor.*;

@UtilityClass
public final class PartyItems {

    public static final ItemStack PARTY_INFO = new ItemStack(Material.NETHER_STAR);;
    public static final ItemStack LEAVE_PARTY_ITEM = new ItemStack(Material.INK_SACK, 1, DyeColor.RED.getDyeData());
    public static final ItemStack ASSIGN_CLASSES = new ItemStack(Material.MAGMA_CREAM);
    public static final ItemStack START_TEAM_SPLIT_ITEM = new ItemStack(Material.DIAMOND_SWORD);
    public static final ItemStack START_FFA_ITEM = new ItemStack(Material.GOLD_SWORD);
    public static final ItemStack OTHER_PARTIES_ITEM = new ItemStack(Material.SKULL_ITEM);

    static {
        ItemUtils.setDisplayName(PARTY_INFO, GRAY + "• " + AQUA + "View Party" + GRAY + " •");
        ItemUtils.setDisplayName(LEAVE_PARTY_ITEM, GRAY + "• " + RED + "Leave Party" + GRAY + " •");
        ItemUtils.setDisplayName(ASSIGN_CLASSES, GRAY + "• " + AQUA + "Teamfight Kits" + GRAY + " •");
        ItemUtils.setDisplayName(START_TEAM_SPLIT_ITEM, GRAY + "• " + AQUA + "Start Team Split" + GRAY + " •");
        ItemUtils.setDisplayName(START_FFA_ITEM, GRAY + "• " + AQUA + "Start Party FFA" + GRAY + " •");
        ItemUtils.setDisplayName(OTHER_PARTIES_ITEM, GRAY + "• " + AQUA + "Other Parties" + GRAY + " •");
    }

}

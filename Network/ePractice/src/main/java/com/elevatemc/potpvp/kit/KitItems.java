package com.elevatemc.potpvp.kit;

import lombok.experimental.UtilityClass;
import com.elevatemc.elib.util.ItemUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;


import static org.bukkit.ChatColor.AQUA;
import static org.bukkit.ChatColor.GRAY;

@UtilityClass
public final class KitItems {

    public static final ItemStack OPEN_EDITOR_ITEM = new ItemStack(Material.BOOK);
    public static final ItemStack ANTIDOTE_ITEM = new ItemStack(Material.POTION, 1, (short) 8196);

    static {
        ItemUtils.setDisplayName(OPEN_EDITOR_ITEM, GRAY + "• " + AQUA + "Kit Editor" + GRAY + " •");
        ItemUtils.setDisplayName(ANTIDOTE_ITEM, ChatColor.GREEN + "Antidote");
    }
}
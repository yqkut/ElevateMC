package com.elevatemc.elib.util;

import org.bukkit.inventory.ItemStack;

/**
 * @author ImHacking
 * @date 5/5/2022
 */
public class LoreUtil {

    public static String getFirstLoreLine(ItemStack itemStack) {
        return getLoreLine(itemStack, 0);
    }

    public static String getLoreLine(ItemStack itemStack, int index) {
        if (!itemStack.hasItemMeta() || !itemStack.getItemMeta().hasLore()) {
            return null;
        }

        if (index >= itemStack.getItemMeta().getLore().size()) {
            return null;
        }

        return itemStack.getItemMeta().getLore().get(index);
    }
}

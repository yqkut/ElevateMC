package com.elevatemc.potpvp.events;

import com.elevatemc.elib.util.ItemBuilder;
import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import static org.bukkit.ChatColor.AQUA;
import static org.bukkit.ChatColor.GRAY;

@UtilityClass
public final class EventItems {

    public ItemStack getEventItem() {
        return ItemBuilder.of(Material.DIAMOND).name(GRAY + "• " + AQUA + "Join an Event" + GRAY + " •").build();
    }

}
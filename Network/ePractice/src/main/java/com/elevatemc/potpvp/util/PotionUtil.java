package com.elevatemc.potpvp.util;

import lombok.experimental.UtilityClass;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.EntityPotion;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@UtilityClass
public final class PotionUtil {

    // Using regular Player#shootProjectile api from spigot doesn't work great because we are not able to change the appearance of the potion. The potion would already spawn on the player side before the appearance has been set. Love spigot...
    public void splashPotion(Player player, ItemStack item) {
        World world = ((CraftWorld) player.getWorld()).getHandle();
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        Entity launch = new EntityPotion(world, entityPlayer, CraftItemStack.asNMSCopy(item));
        world.addEntity(launch);
    }
}

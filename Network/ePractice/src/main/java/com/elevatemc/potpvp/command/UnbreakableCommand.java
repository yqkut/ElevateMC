package com.elevatemc.potpvp.command;

import com.elevatemc.elib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class UnbreakableCommand {
    @Command(names = {"unbreakable"}, permission = "op")
    public static void unbreakable(Player sender) {
        ItemStack hand = sender.getItemInHand();
        if (hand == null || hand.getType().equals(Material.AIR)) {
            sender.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You must hold an item to make it unbreakable!");
            return;
        }

        ItemMeta meta = hand.getItemMeta();
        meta.spigot().setUnbreakable(true);
        hand.setItemMeta(meta);
        sender.sendMessage(ChatColor.GREEN + "The item is now unbreakable!");
    }
}

package com.elevatemc.elib.command.param.defaults;

import com.elevatemc.elib.command.param.ParameterType;
import com.elevatemc.elib.util.EnchantmentWrapper;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;

public class EnchantmentParameterType implements ParameterType<Enchantment> {

    @Override
    public Enchantment transform(CommandSender sender,String source) {

        final EnchantmentWrapper toReturn = EnchantmentWrapper.parse(source);

        if (toReturn == null) {
            sender.sendMessage(ChatColor.RED + "Enchant " + ChatColor.YELLOW + source + ChatColor.RED + " not found.");
            return null;
        } else {
            return toReturn.getBukkitEnchantment();
        }

    }
}

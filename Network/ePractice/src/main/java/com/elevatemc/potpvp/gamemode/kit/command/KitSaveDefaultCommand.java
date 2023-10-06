package com.elevatemc.potpvp.gamemode.kit.command;

import com.elevatemc.elib.command.Command;
import com.elevatemc.elib.command.param.Parameter;
import com.elevatemc.potpvp.gamemode.kit.GameModeKit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class KitSaveDefaultCommand {

    @Command(names = "kit savedefault", permission = "op")
    public static void kitSaveDefault(Player sender, @Parameter(name="gamemode kit") GameModeKit kit) {
        kit.setDefaultArmor(sender.getInventory().getArmorContents());
        kit.setDefaultInventory(sender.getInventory().getContents());
        kit.saveAsync();

        sender.sendMessage(ChatColor.YELLOW + "Saved default armor/inventory for " + kit.getId() + ".");
    }

}
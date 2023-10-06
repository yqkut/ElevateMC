package com.elevatemc.potpvp.gamemode.kit.command;

import com.elevatemc.elib.command.Command;
import com.elevatemc.elib.command.param.Parameter;
import com.elevatemc.potpvp.gamemode.kit.GameModeKit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class KitLoadDefaultCommand {

    @Command(names = "kit loaddefault", permission = "op")
    public static void kitLoadDefault(Player sender, @Parameter(name="gamemode kit") GameModeKit kit) {
        sender.getInventory().setArmorContents(kit.getDefaultArmor());
        sender.getInventory().setContents(kit.getDefaultInventory());
        sender.updateInventory();

        sender.sendMessage(ChatColor.YELLOW + "Loaded default armor/inventory for " + kit.getId() + ".");
    }

}
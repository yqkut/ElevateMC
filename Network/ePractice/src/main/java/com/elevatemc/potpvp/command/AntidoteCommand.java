package com.elevatemc.potpvp.command;

import com.elevatemc.potpvp.kit.KitItems;
import com.elevatemc.elib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class AntidoteCommand {
    @Command(names = {"antidote"}, permission = "op")
    public static void antidote(Player sender) {
        sender.getInventory().addItem(KitItems.ANTIDOTE_ITEM);
        sender.sendMessage(ChatColor.GREEN + "You received an antidote!");
    }
}

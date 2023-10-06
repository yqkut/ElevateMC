package com.elevatemc.elib.skinfix;


import com.elevatemc.elib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class SkinFixCommand {
    @Command(names = {"disableskinfix"}, permission = "op", hidden = true)
    public static void execute(Player player) {
        if (SkinFixHandler.skinFix) {
            SkinFixHandler.skinFix = false;
            player.sendMessage(ChatColor.RED + "Disabled Skin Fix");
        } else {
            SkinFixHandler.skinFix = true;
            player.sendMessage(ChatColor.GREEN + "Enabled Skin Fix");
        }

    }

}

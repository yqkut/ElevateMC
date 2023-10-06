package com.elevatemc.potpvp.command;

import com.elevatemc.elib.command.Command;
import com.elevatemc.potpvp.lobby.menu.HelpMenu;
import org.bukkit.entity.Player;

public class HelpCommand {
    @Command(names = {"help", "?", "halp", "helpme"}, permission = "")
    public static void help(Player sender) {
        new HelpMenu().openMenu(sender);
    }
}

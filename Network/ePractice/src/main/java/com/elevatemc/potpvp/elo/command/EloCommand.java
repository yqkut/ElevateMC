package com.elevatemc.potpvp.elo.command;

import com.elevatemc.elib.command.Command;
import com.elevatemc.elib.command.param.Parameter;
import com.elevatemc.potpvp.lobby.menu.StatisticsMenu;
import org.bukkit.entity.Player;

public class EloCommand {
    @Command(names = {"elo"}, permission = "")
    public static void eloCommand(Player sender, @Parameter(name = "target", defaultValue = "self")Player target) {
        new StatisticsMenu(target).openMenu(sender);
    }
}

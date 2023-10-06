package com.elevatemc.potpvp.arena.command;

import com.elevatemc.elib.command.Command;
import com.elevatemc.potpvp.arena.menu.manageschematics.ManageSchematicsMenu;
import org.bukkit.entity.Player;

public class ArenaManageCommand {
    @Command(names = { "arena manage" }, permission = "op", description = "Manage the arena instances")
    public static void arenaListArenas(Player sender) {
        new ManageSchematicsMenu().openMenu(sender);
    }

}

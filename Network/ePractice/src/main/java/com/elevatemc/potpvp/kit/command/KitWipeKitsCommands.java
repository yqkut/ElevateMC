package com.elevatemc.potpvp.kit.command;

import com.elevatemc.elib.util.UUIDUtils;
import com.elevatemc.potpvp.PotPvPSI;

import com.elevatemc.elib.command.Command;
import com.elevatemc.elib.command.param.Parameter;
import com.elevatemc.potpvp.gamemode.kit.GameModeKit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public final class KitWipeKitsCommands {

    @Command(names = "kit wipetype", permission = "op")
    public static void kitWipeKitsType(Player sender, @Parameter(name="gamemode kit") GameModeKit gameModeKit) {
        int modified = PotPvPSI.getInstance().getKitHandler().wipeKitsWithType(gameModeKit);
        sender.sendMessage(ChatColor.YELLOW + "Wiped " + modified + " " + gameModeKit.getId() + " kits.");
    }

    @Command(names = "kit wipeplayer", permission = "op")
    public static void kitWipeKitsPlayer(Player sender, @Parameter(name="target") UUID target) {
        PotPvPSI.getInstance().getKitHandler().wipeKitsForPlayer(target);
        sender.sendMessage(ChatColor.YELLOW + "Wiped " + UUIDUtils.name(target) + "'s kits.");
    }

}
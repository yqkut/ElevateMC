package com.elevatemc.potpvp.postmatchinv.command;

import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.postmatchinv.PostMatchInvHandler;
import com.elevatemc.potpvp.postmatchinv.PostMatchPlayer;
import com.elevatemc.potpvp.postmatchinv.menu.PostMatchMenu;
import com.elevatemc.elib.command.Command;
import com.elevatemc.elib.command.param.Parameter;
import com.elevatemc.elib.util.UUIDUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public final class CheckPostMatchInvCommand {

    @Command(names = { "checkPostMatchInv", "_" }, permission = "")
    public static void checkPostMatchInv(Player sender, @Parameter(name = "target") UUID target) {
        PostMatchInvHandler postMatchInvHandler = PotPvPSI.getInstance().getPostMatchInvHandler();
        Map<UUID, PostMatchPlayer> players = postMatchInvHandler.getPostMatchData(sender.getUniqueId());

        if (players.containsKey(target)) {
            new PostMatchMenu(players.get(target)).openMenu(sender);
        } else {
            sender.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "Data for " + UUIDUtils.name(target) + "'s last match inventory could not be found.");
        }
    }
}
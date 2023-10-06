package com.elevatemc.potpvp.follow.command;

import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.follow.FollowHandler;
import com.elevatemc.potpvp.match.MatchHandler;
import com.elevatemc.potpvp.setting.Setting;
import com.elevatemc.potpvp.setting.SettingHandler;
import com.elevatemc.potpvp.validation.PotPvPValidation;
import com.elevatemc.elib.command.Command;
import com.elevatemc.elib.command.param.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class FollowCommand {

    @Command(names={"follow"}, permission="")
    public static void follow(Player sender, @Parameter(name="target") Player target) {
        if (!PotPvPValidation.canFollowSomeone(sender)) {
            return;
        }

        FollowHandler followHandler = PotPvPSI.getInstance().getFollowHandler();
        SettingHandler settingHandler = PotPvPSI.getInstance().getSettingHandler();
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();

        if (sender == target) {
            sender.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You cannot follow yourself.");
            return;
        } else if (!settingHandler.getSetting(target, Setting.ALLOW_SPECTATORS)) {
            if (sender.isOp()) {
                sender.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "Bypassing " + target.getName() + "'s no spectators preference.");
            } else {
                sender.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + target.getName() + " has their spectators setting turned off right now.");
                return;
            }
        }

        followHandler.getFollowing(sender).ifPresent(fo -> UnfollowCommand.unfollow(sender));

        if (matchHandler.isSpectatingMatch(sender)) {
            matchHandler.getMatchSpectating(sender).removeSpectator(sender);
        }

        followHandler.startFollowing(sender, target);
    }
}
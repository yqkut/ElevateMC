package com.elevatemc.potpvp.setting.command;

import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.setting.Setting;
import com.elevatemc.potpvp.setting.SettingHandler;
import com.elevatemc.elib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * /toggleduels command, allows players to toggle {@link Setting#RECEIVE_DUELS} setting
 */
public final class ToggleDuelCommand {

    @Command(names = { "toggleduels" }, permission = "")
    public static void toggleDuel(Player sender) {
        if (!Setting.RECEIVE_DUELS.canUpdate(sender)) {
            sender.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "No permission.");
            return;
        }

        SettingHandler settingHandler = PotPvPSI.getInstance().getSettingHandler();
        boolean enabled = !settingHandler.getSetting(sender, Setting.RECEIVE_DUELS);

        settingHandler.updateSetting(sender, Setting.RECEIVE_DUELS, enabled);

        if (enabled) {
            sender.sendMessage(ChatColor.GREEN + "Your duel requests have been turned on.");
        } else {
            sender.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "Your duel requests have been turned off.");
        }
    }

}
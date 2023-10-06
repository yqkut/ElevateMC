package com.elevatemc.potpvp.setting.command;

import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.setting.Setting;
import com.elevatemc.potpvp.setting.SettingHandler;
import com.elevatemc.elib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * /night command, allows players to toggle {@link Setting#NIGHT_MODE} setting
 */
public final class NightCommand {

    @Command(names = { "night", "nightMode" }, permission = "")
    public static void night(Player sender) {
        if (!Setting.NIGHT_MODE.canUpdate(sender)) {
            sender.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "No permission.");
            return;
        }

        SettingHandler settingHandler = PotPvPSI.getInstance().getSettingHandler();
        boolean enabled = !settingHandler.getSetting(sender, Setting.NIGHT_MODE);

        settingHandler.updateSetting(sender, Setting.NIGHT_MODE, enabled);

        if (enabled) {
            sender.sendMessage(ChatColor.GREEN + "Night mode turned on.");
        } else {
            sender.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "Night mode turned off.");
        }
    }

}
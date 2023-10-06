package com.elevatemc.potpvp.command;

import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.util.VisibilityUtils;
import com.elevatemc.elib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

public final class ModCommand {

    @Command(names = {"mod", "h", "staff", "silent"}, permission = "core.staffteam")
    public static void silent(Player sender) {
        if (sender.hasMetadata("modmode")) {
            sender.removeMetadata("modmode", PotPvPSI.getInstance());
            sender.removeMetadata("invisible", PotPvPSI.getInstance());

            sender.sendMessage(ChatColor.GOLD + "Mod Mode" + ChatColor.GRAY + ": " + ChatColor.RED + "Disabled");

        } else {
            sender.setMetadata("modmode", new FixedMetadataValue(PotPvPSI.getInstance(), true));
            sender.setMetadata("invisible", new FixedMetadataValue(PotPvPSI.getInstance(), true));
            
            sender.sendMessage(ChatColor.GOLD + "Mod Mode" + ChatColor.GRAY + ": " + ChatColor.GREEN + "Enabled");
        }
        VisibilityUtils.updateVisibility(sender);
    }
}
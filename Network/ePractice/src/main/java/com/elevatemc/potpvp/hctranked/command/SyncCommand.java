package com.elevatemc.potpvp.hctranked.command;

import com.elevatemc.elib.eLib;
import com.elevatemc.elib.command.Command;
import com.elevatemc.potpvp.PotPvPSI;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import redis.clients.jedis.params.SetParams;

public class SyncCommand {
    @Command(names = {"discord"}, permission = "")
    public static void sync(Player sender) {
        if (PotPvPSI.getInstance().getHCTRankedHandler().getSyncHandler().isSynced(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You are already synced! If you wish to unlink your account message an admin.");
            return;
        }
        eLib.getInstance().runRedisCommand(redis -> {
            String oldCode = redis.get("rankedhcf.sync.player." + sender.getUniqueId().toString());
            if (oldCode != null) {
                sender.sendMessage(ChatColor.GREEN + "You already have a code. Please type " + ChatColor.RED + "/register " + oldCode + ChatColor.GREEN + " in the " + ChatColor.RED + "#register" + ChatColor.GREEN + " channel!");
                return null;
            }
            int code = PotPvPSI.RANDOM.nextInt(999999);
            redis.set("rankedhcf.sync.player." + sender.getUniqueId().toString(), String.valueOf(code), new SetParams().ex(60 * 5));
            redis.set("rankedhcf.sync.code." + code, sender.getUniqueId().toString(), new SetParams().ex(60 * 5));
            sender.sendMessage(ChatColor.GREEN + "A code has been generated for you. Please type " + ChatColor.RED + "/register " + code + ChatColor.GREEN + " in the " + ChatColor.RED + "#register" + ChatColor.GREEN + " channel!");
            return null;
        });
    }
}

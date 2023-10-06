package com.elevatemc.potpvp.command;

import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.elo.repository.EloRepository;
import com.elevatemc.potpvp.gamemode.GameMode;
import com.elevatemc.elib.command.Command;
import com.google.common.collect.ImmutableSet;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.Map;

public final class EloConvertCommand {

    @Command(names = {"eloconvert"}, permission = "op")
    public static void eloconvert(Player sender) {
        OfflinePlayer[] offlinePlayers = Bukkit.getOfflinePlayers();

        EloRepository repository = PotPvPSI.getInstance().getEloHandler().getEloRepository();

        for (int i = 0; i < offlinePlayers.length; i++) {
            OfflinePlayer target = offlinePlayers[i];

            if (i % 100 == 0) {
                sender.sendMessage(ChatColor.GREEN + "Converting: " + i + "/" + offlinePlayers.length);
            }

            try {
                Map<GameMode, Integer> map = repository.loadElo(ImmutableSet.of(target.getUniqueId()));
                repository.saveElo(ImmutableSet.of(target.getUniqueId()), map);
            } catch (IOException e) {
                sender.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "An error occured.");
                e.printStackTrace();
            }
        }
    }

}

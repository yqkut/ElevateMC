package com.elevatemc.potpvp.events;

import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.events.game.Game;
import com.elevatemc.potpvp.events.game.GameQueue;
import com.elevatemc.potpvp.lobby.LobbyHandler;
import com.elevatemc.potpvp.lobby.LobbyUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class EventTask extends BukkitRunnable {

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            LobbyHandler handler = PotPvPSI.getInstance().getLobbyHandler();

            if (handler.isInLobby(player)) {
                if(!GameQueue.INSTANCE.getGames().isEmpty() || !GameQueue.INSTANCE.getRunningGames().isEmpty()) {
                    if (!player.getInventory().contains(EventItems.getEventItem()) && !PotPvPSI.getInstance().getPartyHandler().hasParty(player)) {
                        LobbyUtils.resetInventory(player);
                    }
                }

            } else {
                Game game = GameQueue.INSTANCE.getCurrentGame(player);
                if (game != null && game.getPlayers().contains(player) && player.getInventory().contains(EventItems.getEventItem())) {
                    player.getInventory().remove(Material.DIAMOND);
                }
            }

        }
    }
}

package com.elevatemc.potpvp.match.listener;

import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.match.Match;
import com.elevatemc.potpvp.match.MatchHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;

public final class MatchHardcoreHealingListener implements Listener {

    @EventHandler
    public void onEntityRegainHealth(EntityRegainHealthEvent event) {
        if (!(event.getEntity() instanceof Player) || event.getRegainReason() != EntityRegainHealthEvent.RegainReason.SATIATED) {
            return;
        }

        Player player = (Player) event.getEntity();
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();

        if (!matchHandler.isPlayingMatch(player)) {
            return;
        }

        Match match = matchHandler.getMatchPlaying(player);

        if (match.getGameMode().getHardcoreHealing()) {
            event.setCancelled(true);
        }
    }

}

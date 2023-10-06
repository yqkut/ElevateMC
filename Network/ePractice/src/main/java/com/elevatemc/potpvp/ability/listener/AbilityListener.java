package com.elevatemc.potpvp.ability.listener;

import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.ability.Ability;
import com.elevatemc.potpvp.ability.listener.events.AbilityUseEvent;
import com.elevatemc.potpvp.gamemode.GameMode;
import com.elevatemc.potpvp.gamemode.GameModes;
import com.elevatemc.potpvp.match.Match;
import com.elevatemc.potpvp.match.MatchTeam;
import com.elevatemc.potpvp.match.event.MatchEndEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class AbilityListener implements Listener {

    @EventHandler(priority = EventPriority.LOW)
    private void onUse(AbilityUseEvent event) {
        final Player player = event.getPlayer();
        final Ability ability = event.getAbility();

        if (ability.hasCooldown(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onMatchEnd(MatchEndEvent event) {
        Match match = event.getMatch();
        GameMode gameMode = match.getGameMode();
        if (gameMode.equals(GameModes.TRAPPING)) {
            for (MatchTeam team : match.getTeams()) {
                team.forEachAlive(player -> {
                    PotPvPSI.getInstance().getAbilityHandler().resetCooldowns(player);
                });
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Match match = PotPvPSI.getInstance().getMatchHandler().getMatchPlaying(event.getEntity());
        if (match == null) return;

        GameMode gameMode = match.getGameMode();
        if (gameMode.equals(GameModes.TRAPPING)) {
            PotPvPSI.getInstance().getAbilityHandler().resetCooldowns(event.getEntity());
        }
    }
}

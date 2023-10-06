package com.elevatemc.potpvp.gamemodes.combo.listener;

import com.elevatemc.potpvp.match.Match;
import com.elevatemc.potpvp.match.event.MatchStartEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Objects;

public class MatchComboListener implements Listener {
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onStart(MatchStartEvent event) {
        Match match = event.getMatch();

        int noDamageTicks = match.getGameMode().getId().contains("COMBO") ? 3 : 20;
        match.getTeams().forEach(team -> team.getAliveMembers().stream().map(Bukkit::getPlayer).filter(Objects::nonNull).forEach(p -> p.setMaximumNoDamageTicks(noDamageTicks)));
    }
}
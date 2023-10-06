package com.elevatemc.potpvp.match.rematch.listener;

import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.match.event.MatchTerminateEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public final class RematchGeneralListener implements Listener {

    @EventHandler
    public void onMatchTerminate(MatchTerminateEvent event) {
        PotPvPSI.getInstance().getRematchHandler().registerRematches(event.getMatch());
    }

}
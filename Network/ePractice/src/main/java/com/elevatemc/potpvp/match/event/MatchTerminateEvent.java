package com.elevatemc.potpvp.match.event;

import lombok.Getter;
import com.elevatemc.potpvp.match.Match;
import org.bukkit.event.HandlerList;

/**
 * Called when a match is terminated (when its {@link com.elevatemc.potpvp.match.MatchState} changes
 * to {@link com.elevatemc.potpvp.match.MatchState#TERMINATED})
 * @see com.elevatemc.potpvp.match.MatchState#TERMINATED
 */
public final class MatchTerminateEvent extends MatchEvent {

    @Getter private static HandlerList handlerList = new HandlerList();


    public MatchTerminateEvent(Match match) {
        super(match);
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

}
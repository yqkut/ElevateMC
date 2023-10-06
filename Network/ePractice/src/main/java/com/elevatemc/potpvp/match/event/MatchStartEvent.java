package com.elevatemc.potpvp.match.event;

import lombok.Getter;
import com.elevatemc.potpvp.match.Match;
import org.bukkit.event.HandlerList;

/**
 * Called when a match's countdown ends (when its {@link com.elevatemc.potpvp.match.MatchState} changes
 * to {@link com.elevatemc.potpvp.match.MatchState#IN_PROGRESS})
 * @see com.elevatemc.potpvp.match.MatchState#IN_PROGRESS
 */
public final class MatchStartEvent extends MatchEvent {

    @Getter private static HandlerList handlerList = new HandlerList();

    public MatchStartEvent(Match match) {
        super(match);
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

}
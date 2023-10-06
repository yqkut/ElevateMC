package com.elevatemc.potpvp.match.event;

import lombok.Getter;
import com.elevatemc.potpvp.match.Match;
import org.bukkit.event.HandlerList;

/**
 * Called when a match is ended (when its {@link com.elevatemc.potpvp.match.MatchState} changes
 * to {@link com.elevatemc.potpvp.match.MatchState#ENDING})
 * @see com.elevatemc.potpvp.match.MatchState#ENDING
 */
public final class MatchEndEvent extends MatchEvent {

    @Getter private static HandlerList handlerList = new HandlerList();

    public MatchEndEvent(Match match) {
        super(match);
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

}
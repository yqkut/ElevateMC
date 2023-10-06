package com.elevatemc.potpvp.match.event;

import lombok.Getter;
import com.elevatemc.potpvp.match.Match;
import org.bukkit.event.HandlerList;

/**
 * Called when a match's countdown starts (when its {@link com.elevatemc.potpvp.match.MatchState} changes
 * to {@link com.elevatemc.potpvp.match.MatchState#COUNTDOWN})
 * @see com.elevatemc.potpvp.match.MatchState#COUNTDOWN
 */
public final class MatchCountdownStartEvent extends MatchEvent {

    @Getter private static HandlerList handlerList = new HandlerList();

    public MatchCountdownStartEvent(Match match) {
        super(match);
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

}
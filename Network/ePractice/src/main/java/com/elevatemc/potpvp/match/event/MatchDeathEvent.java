package com.elevatemc.potpvp.match.event;

import com.elevatemc.potpvp.match.Match;
import com.elevatemc.potpvp.match.MatchTeam;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

/**
 * Called when a match's countdown ends (when its {@link com.elevatemc.potpvp.match.MatchState} changes
 * to {@link com.elevatemc.potpvp.match.MatchState#IN_PROGRESS})
 * @see com.elevatemc.potpvp.match.MatchState#IN_PROGRESS
 */
public final class MatchDeathEvent extends MatchEvent {

    @Getter private static HandlerList handlerList = new HandlerList();
    @Getter private Player target;
    @Getter private Player killer;
    @Getter private DeathCause deathCause;

    public MatchDeathEvent(Match match, Player target, Player killer, DeathCause deathCause) {
        super(match);
        this.target = target;
        this.killer = killer;

    }

    public MatchTeam findTargetTeam() {
        return getMatch().getTeam(target.getUniqueId());
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public enum DeathCause {
        PLAYER_KILL,
        DISCONNECT,
        OTHER_DAMAGE,
        KICK,
        VOID
    }

}
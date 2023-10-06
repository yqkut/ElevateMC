package com.elevatemc.potpvp.party.event;

import lombok.Getter;
import com.elevatemc.potpvp.party.Party;
import org.bukkit.event.HandlerList;

/**
 * Called when a {@link Party} is disbanded.
 * @see com.elevatemc.potpvp.party.command.PartyDisbandCommand
 * @see Party#disband()
 */
public final class PartyDisbandEvent extends PartyEvent {

    @Getter private static HandlerList handlerList = new HandlerList();

    public PartyDisbandEvent(Party party) {
        super(party);
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

}
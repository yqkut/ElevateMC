package com.elevatemc.potpvp.party.event;

import lombok.Getter;
import com.elevatemc.potpvp.party.Party;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

/**
 * Called when a {@link Party} is created.
 * @see com.elevatemc.potpvp.party.command.PartyCreateCommand
 * @see com.elevatemc.potpvp.party.PartyHandler#getOrCreateParty(Player)
 */
public final class PartyCreateEvent extends PartyEvent {

    @Getter private static HandlerList handlerList = new HandlerList();

    public PartyCreateEvent(Party party) {
        super(party);
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

}
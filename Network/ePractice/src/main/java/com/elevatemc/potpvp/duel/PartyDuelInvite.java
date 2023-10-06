package com.elevatemc.potpvp.duel;

import com.elevatemc.potpvp.gamemode.GameMode;
import com.elevatemc.potpvp.party.Party;

public final class PartyDuelInvite extends DuelInvite<Party> {

    public PartyDuelInvite(Party sender, Party target, GameMode gameModes, String arenaName) {
        super(sender, target, gameModes, arenaName);
    }

}
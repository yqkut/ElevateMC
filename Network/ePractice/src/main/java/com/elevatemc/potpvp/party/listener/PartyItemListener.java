package com.elevatemc.potpvp.party.listener;

import com.elevatemc.potpvp.party.PartyHandler;
import com.elevatemc.potpvp.party.PartyItems;
import com.elevatemc.potpvp.party.command.PartyFfaCommand;
import com.elevatemc.potpvp.party.command.PartyInfoCommand;
import com.elevatemc.potpvp.party.command.PartyLeaveCommand;
import com.elevatemc.potpvp.party.command.PartyTeamSplitCommand;
import com.elevatemc.potpvp.party.menu.RosterMenu;
import com.elevatemc.potpvp.party.menu.otherparties.OtherPartiesMenu;
import com.elevatemc.potpvp.util.ItemListener;

public final class PartyItemListener extends ItemListener {

    public PartyItemListener(PartyHandler partyHandler) {
        addHandler(PartyItems.PARTY_INFO, p -> { PartyInfoCommand.partyInfo(p, p); });
        addHandler(PartyItems.LEAVE_PARTY_ITEM, PartyLeaveCommand::partyLeave);
        addHandler(PartyItems.START_TEAM_SPLIT_ITEM, PartyTeamSplitCommand::partyTeamSplit);
        addHandler(PartyItems.START_FFA_ITEM, PartyFfaCommand::partyFfa);
        addHandler(PartyItems.OTHER_PARTIES_ITEM, p -> new OtherPartiesMenu().openMenu(p));
        addHandler(PartyItems.ASSIGN_CLASSES, p -> new RosterMenu(partyHandler.getParty(p)).openMenu(p));
    }
}
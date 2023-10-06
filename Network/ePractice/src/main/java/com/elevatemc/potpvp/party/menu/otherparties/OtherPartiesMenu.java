package com.elevatemc.potpvp.party.menu.otherparties;

import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.lobby.LobbyHandler;
import com.elevatemc.potpvp.party.Party;
import com.elevatemc.potpvp.party.PartyHandler;
import com.elevatemc.potpvp.setting.Setting;
import com.elevatemc.potpvp.setting.SettingHandler;
import com.elevatemc.elib.menu.Button;
import com.elevatemc.elib.menu.pagination.PaginatedMenu;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;

public final class OtherPartiesMenu extends PaginatedMenu {

    public OtherPartiesMenu() {
        setPlaceholder(true);
        setAutoUpdate(true);
    }

    @Override
    public String getPrePaginatedTitle(Player player) {
        return ChatColor.BLUE + "Other parties";
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        SettingHandler settingHandler = PotPvPSI.getInstance().getSettingHandler();
        PartyHandler partyHandler = PotPvPSI.getInstance().getPartyHandler();
        LobbyHandler lobbyHandler = PotPvPSI.getInstance().getLobbyHandler();

        Map<Integer, Button> buttons = new HashMap<>();
        List<Party> parties = new ArrayList<>(partyHandler.getParties());
        int index = 0;

        parties.sort(Comparator.comparing(p -> p.getMembers().size()));

        for (Party party : parties) {
            if (party.isMember(player.getUniqueId())) {
                continue;
            }

            if (!lobbyHandler.isInLobby(Bukkit.getPlayer(party.getLeader()))) {
                continue;
            }

            if (!settingHandler.getSetting(Bukkit.getPlayer(party.getLeader()), Setting.RECEIVE_DUELS)) {
                continue;
            }

            /* if (PotPvPSI.getInstance().getTournamentHandler().isInTournament(party)) {
                continue;
            } */

            buttons.put(index++, new OtherPartyButton(party));
        }

        return buttons;
    }

    // we lock the size of this inventory at full, otherwise we'll have
    // issues if it 'grows' into the next line while it's open (say we open
    // the menu with 8 entries, then it grows to 11 [and onto the second row]
    // - this breaks things)
    @Override
    public int size(Player buttons) {
        return 9 * 6;
    }

    @Override
    public int getMaxItemsPerPage(Player player) {
        return 9 * 5; // top row is dedicated to switching
    }
}
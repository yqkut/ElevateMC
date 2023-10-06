package com.elevatemc.potpvp.tournament.menu;

import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.match.Match;
import com.elevatemc.potpvp.match.MatchState;
import com.elevatemc.potpvp.setting.SettingHandler;
import com.elevatemc.elib.menu.Button;
import com.elevatemc.elib.menu.pagination.PaginatedMenu;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class StatusMenu extends PaginatedMenu {

    public StatusMenu() {
        setAutoUpdate(true);
    }

    @Override
    public String getPrePaginatedTitle(Player player) {
        return "Tournament Status";
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        int i = 0;

        List<Match> matches = PotPvPSI.getInstance().getTournamentHandler().getTournament().getMatches().stream().filter(m -> m.getState() != MatchState.TERMINATED && m.getState() != MatchState.ENDING).collect(Collectors.toList());

        for (Match match : matches) {
            // players can view this menu while spectating
            if (match.isSpectator(player.getUniqueId())) {
                continue;
            }

            buttons.put(i++, new StatusButton(match));
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

}
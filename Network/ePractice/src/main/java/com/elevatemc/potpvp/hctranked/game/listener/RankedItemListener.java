package com.elevatemc.potpvp.hctranked.game.listener;

import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.hctranked.command.RankedCommand;
import com.elevatemc.potpvp.hctranked.game.RankedGameItems;
import com.elevatemc.potpvp.hctranked.game.menu.KitsMenu;
import com.elevatemc.potpvp.util.ItemListener;

public class RankedItemListener extends ItemListener {
    public RankedItemListener() {
        addHandler(RankedGameItems.GAME_INFO, p -> RankedCommand.info(p, p));
        addHandler(RankedGameItems.ASSIGN_CLASSES, p -> {
            new KitsMenu(PotPvPSI.getInstance().getHCTRankedHandler().getGameHandler().getJoinedGame(p).getTeam(p)).openMenu(p);
        });
        addHandler(RankedGameItems.LEAVE_GAME, RankedCommand::leave);

        addHandler(RankedGameItems.READY, RankedCommand::ready);
        addHandler(RankedGameItems.NOT_READY, RankedCommand::ready);
    }
}

package com.elevatemc.potpvp.kit.listener;

import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.gamemode.kit.GameModeKit;
import com.elevatemc.potpvp.gamemode.menu.editing.EditGameModeMenu;
import com.elevatemc.potpvp.gamemode.menu.editing.EditGameModeKitMenu;
import com.elevatemc.potpvp.kit.KitItems;
import com.elevatemc.potpvp.kit.menu.kits.KitsMenu;
import com.elevatemc.potpvp.lobby.LobbyHandler;
import com.elevatemc.potpvp.util.ItemListener;

public final class KitItemListener extends ItemListener {

    public KitItemListener() {
        addHandler(KitItems.OPEN_EDITOR_ITEM, player -> {
            LobbyHandler lobbyHandler = PotPvPSI.getInstance().getLobbyHandler();

            if (lobbyHandler.isInLobby(player)) {
                new EditGameModeMenu(gameMode -> {
                    if (gameMode.getKits().size() > 1) {
                        new EditGameModeKitMenu(gameModeKit -> {
                            new KitsMenu(gameModeKit).openMenu(player);
                        }, gameMode).openMenu(player);
                    } else {
                        new KitsMenu(GameModeKit.byId(gameMode.getId())).openMenu(player);
                    }
                }).openMenu(player);
            }
        });
    }

}
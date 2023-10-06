package com.elevatemc.potpvp.gamemode.menu.select;

import com.elevatemc.potpvp.gamemode.GameMode;
import com.elevatemc.potpvp.gamemode.GameModes;
import com.elevatemc.potpvp.gamemodes.teamfight.TeamfightDebuff;
import com.google.common.base.Preconditions;
import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.party.Party;
import com.elevatemc.elib.menu.Button;
import com.elevatemc.elib.menu.Menu;
import com.elevatemc.elib.util.Callback;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public final class SelectGameModeMenu extends Menu {

    private final String title;
    private final Callback<GameMode> callback;

    public SelectGameModeMenu(Callback<GameMode> callback, String title) {
        this.title = title;
        this.callback = Preconditions.checkNotNull(callback, "callback");

        setPlaceholder(true);
    }

    @Override
    public String getTitle(Player player) {
        return ChatColor.BLUE + title;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        int index = 9;


        for (GameMode gameMode : GameMode.getAll()) {
            if (gameMode.equals(GameModes.TEAMFIGHT)) {
                continue;
            }

            index++;
            if ((index + 1) % 9 == 0) index++;
            if (index % 9 == 0) index++;

            buttons.put(index, new SelectGameModeButton(gameMode, callback));
        }

        Party party = PotPvPSI.getInstance().getPartyHandler().getParty(player);
        if (party != null) {
            index++;
            if ((index + 1) % 9 == 0) index++;
            if (index % 9 == 0) index++;
            buttons.put(index, new SelectGameModeButton(GameModes.TEAMFIGHT, callback));
        }

        return buttons;
    }

    @Override
    public int size(Player player) {
        Map<Integer, Button> buttons = getButtons(player);
        return ((int)Math.ceil(((float)buttons.size() / 7)) + 2) * 9;
    }
}
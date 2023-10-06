package com.elevatemc.potpvp.gamemode.menu.editing;

import com.elevatemc.elib.menu.Button;
import com.elevatemc.elib.menu.Menu;
import com.elevatemc.elib.util.Callback;
import com.elevatemc.potpvp.gamemode.GameMode;
import com.elevatemc.potpvp.gamemode.kit.GameModeKit;
import com.google.common.base.Preconditions;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class EditGameModeKitMenu extends Menu {

    private final GameMode gameMode;
    private final Callback<GameModeKit> callback;

    public EditGameModeKitMenu(Callback<GameModeKit> callback, GameMode gameMode) {
        this.gameMode = gameMode;
        this.callback = Preconditions.checkNotNull(callback, "callback");

        setPlaceholder(true);
    }

    @Override
    public String getTitle(Player player) {
        return ChatColor.BLUE + "Kit Editor";
    }

    @Override
    public Map<Integer, Button> getButtons(Player p0) {
        Map<Integer, Button> buttons = new HashMap<>();

        int index = 9;


        for (GameModeKit gameModeKit : gameMode.getKits()) {
            index++;
            if ((index + 1) % 9 == 0) index++;
            if (index % 9 == 0) index++;

            buttons.put(index, new GameModeKitButton(gameModeKit, callback));
        }

        return buttons;
    }

    @Override
    public int size(Player player) {
        return ((int)Math.ceil(((float)getButtons(player).size() / 7)) + 2) * 9;
    }
}
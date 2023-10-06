package com.elevatemc.potpvp.gamemode.menu.queue;

import com.elevatemc.potpvp.gamemode.GameMode;
import com.elevatemc.potpvp.gamemode.GameModes;
import com.google.common.base.Preconditions;
import com.elevatemc.elib.menu.Button;
import com.elevatemc.elib.menu.Menu;
import com.elevatemc.elib.util.Callback;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public final class QueueGameModeMenu extends Menu {

    private final Callback<GameMode> callback;
    private final boolean competitive;

    public QueueGameModeMenu(Callback<GameMode> callback, boolean competitive) {
        this.callback = Preconditions.checkNotNull(callback, "callback");
        this.competitive = competitive;

        setPlaceholder(true);
        setAutoUpdate(true);
    }

    @Override
    public String getTitle(Player player) {
        return ChatColor.BLUE + "Play " + (competitive ? "Competitive" : "Casual");
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

            buttons.put(index, new QueueGameModeButton(gameMode, competitive, callback));
        }

        return buttons;
    }

    @Override
    public int size(Player player) {
        Map<Integer, Button> buttons = getButtons(player);
        return ((int)Math.ceil(((float)buttons.size() / 7)) + 2) * 9;
    }
}
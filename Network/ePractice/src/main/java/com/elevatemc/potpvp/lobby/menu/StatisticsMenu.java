package com.elevatemc.potpvp.lobby.menu;

import com.elevatemc.potpvp.gamemode.GameMode;
import com.elevatemc.potpvp.lobby.menu.statistics.GlobalEloButton;
import com.elevatemc.potpvp.lobby.menu.statistics.KitButton;
import com.elevatemc.potpvp.lobby.menu.statistics.PlayerButton;
import com.elevatemc.elib.menu.Button;
import com.elevatemc.elib.menu.Menu;
import com.elevatemc.elib.util.ItemBuilder;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public final class StatisticsMenu extends Menu {
    private Player target;

    public StatisticsMenu(Player target) {
        this.target = target;

        setAutoUpdate(true);
        setPlaceholder(true);
    }

    @Override
    public String getTitle(Player player) {
        return "Statistics";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(getSlot(4, 1), new GlobalEloButton(target));

        int y = 3;
        int x = 1;

        for (GameMode gameMode : GameMode.getAll()) {
            if (!gameMode.getSupportsCompetitive()) continue;

            buttons.put(getSlot(x++, y), new KitButton(gameMode, target));

            if (x == 8) {
                y++;
                x = 1;
            }
        }

        return buttons;
    }

    @Override
    public int size(Player buttons) {
        return 9 * 6;
    }

}
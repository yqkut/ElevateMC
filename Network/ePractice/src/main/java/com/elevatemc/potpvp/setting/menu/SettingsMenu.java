package com.elevatemc.potpvp.setting.menu;

import com.elevatemc.potpvp.setting.Setting;
import com.elevatemc.elib.menu.Button;
import com.elevatemc.elib.menu.Menu;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Menu used by /settings to let players toggle settings
 */
public final class SettingsMenu extends Menu {

    public SettingsMenu() {
        setAutoUpdate(true);
        setPlaceholder(true);
    }

    @Override
    public String getTitle(Player player) {
        return ChatColor.BLUE + "Your Settings";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        int index = 9;

        for (Setting setting : Setting.values()) {
            if (setting.canUpdate(player)) {
                index++;
                if ((index + 1) % 9 == 0) index++;
                if (index % 9 == 0) index++;

                buttons.put(index, new SettingButton(setting));
            }
        }

        return buttons;
    }

    @Override
    public int size(Player player) {
        Map<Integer, Button> buttons = getButtons(player);
        return ((int)Math.ceil(((float)buttons.size() / 7)) + 2) * 9;
    }
}
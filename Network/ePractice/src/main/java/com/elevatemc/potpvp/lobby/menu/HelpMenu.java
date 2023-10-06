package com.elevatemc.potpvp.lobby.menu;

import com.elevatemc.elib.menu.Button;
import com.elevatemc.elib.menu.Menu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class HelpMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return "Help";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(getSlot(4, 0), Button.placeholder(Material.MAP, ChatColor.BLUE + "Help"));

        return buttons;
    }
}
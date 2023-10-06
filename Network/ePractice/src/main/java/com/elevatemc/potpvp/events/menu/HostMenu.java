package com.elevatemc.potpvp.events.menu;

import com.elevatemc.elib.menu.Button;
import com.elevatemc.elib.menu.Menu;
import com.elevatemc.potpvp.events.EventHandler;
import com.elevatemc.potpvp.events.event.GameEvent;
import com.elevatemc.potpvp.util.Color;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class HostMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return Color.translate("&3Host an event");
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> toReturn = new HashMap<>();

        for (GameEvent event : EventHandler.EVENTS) {
            toReturn.put(toReturn.size(), new HostEventButton(event));
        }

        return toReturn;
    }
}

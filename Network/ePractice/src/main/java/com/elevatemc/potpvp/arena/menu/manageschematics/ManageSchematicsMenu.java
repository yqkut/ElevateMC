package com.elevatemc.potpvp.arena.menu.manageschematics;

import com.elevatemc.elib.eLib;
import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.arena.ArenaHandler;
import com.elevatemc.potpvp.arena.ArenaSchematic;
import com.elevatemc.elib.menu.Button;
import com.elevatemc.elib.menu.Menu;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public final class ManageSchematicsMenu extends Menu {

    public ManageSchematicsMenu() {
        setAutoUpdate(true);
    }

    @Override
    public String getTitle(Player player) {
        return "Manage schematics";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        ArenaHandler arenaHandler = PotPvPSI.getInstance().getArenaHandler();
        Map<Integer, Button> buttons = new HashMap<>();
        int index = 0;

        for (ArenaSchematic schematic : arenaHandler.getSchematics()) {
            buttons.put(index++, new ManageSchematicButton(schematic));
        }

        return buttons;
    }

}
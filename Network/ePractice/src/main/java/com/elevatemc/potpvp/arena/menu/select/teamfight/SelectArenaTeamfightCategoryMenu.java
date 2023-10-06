package com.elevatemc.potpvp.arena.menu.select.teamfight;

import com.elevatemc.potpvp.arena.ArenaTeamfightCategory;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.elevatemc.potpvp.arena.ArenaSchematic;
import com.elevatemc.elib.menu.Button;
import com.elevatemc.elib.menu.Menu;
import com.elevatemc.elib.util.Callback;
import com.google.common.collect.ImmutableList;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.*;

public class SelectArenaTeamfightCategoryMenu extends Menu {
    private final Callback<ArenaTeamfightCategory> callback;
    List<ArenaSchematic> possibleSchematics = new ArrayList<>();

    public SelectArenaTeamfightCategoryMenu(Callback<ArenaTeamfightCategory> callback) {
        this.callback = Preconditions.checkNotNull(callback, "callback");
        setPlaceholder(true);
    }

    @Override
    public String getTitle(Player player) {
        return ChatColor.BLUE + "Select a category";
    }

    @Override
    public Map<Integer, Button> getButtons(Player arg0) {
        Map<Integer, Button> buttons = Maps.newHashMap();

        buttons.put(getSlot(0,  2), new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.GREEN + "View All";
            }

            @Override
            public List<String> getDescription(Player player) {
                return ImmutableList.of(
                        ChatColor.DARK_AQUA + "❘ " + ChatColor.WHITE + "Click this to view all the teamfight maps"
                );
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.BRICK;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                callback.callback(null);
            }
        });

        buttons.put(getSlot(1, 1), new ArenaTeamfightCategoryButton(ArenaTeamfightCategory.CITADELS, callback));
        buttons.put(getSlot(3, 1), new ArenaTeamfightCategoryButton(ArenaTeamfightCategory.NETHER, callback));
        buttons.put(getSlot(5, 1), new ArenaTeamfightCategoryButton(ArenaTeamfightCategory.KOTHS, callback));
        buttons.put(getSlot(7, 1), new ArenaTeamfightCategoryButton(ArenaTeamfightCategory.OTHER, callback));

        return buttons;
    }


    @Override
    public int size(Player player) {
        return 9*3;
    }
}

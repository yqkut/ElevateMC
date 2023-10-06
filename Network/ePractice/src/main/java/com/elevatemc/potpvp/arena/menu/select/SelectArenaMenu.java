package com.elevatemc.potpvp.arena.menu.select;

import com.elevatemc.potpvp.arena.ArenaTeamfightCategory;
import com.elevatemc.potpvp.gamemode.GameMode;
import com.elevatemc.potpvp.gamemode.GameModes;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.arena.ArenaSchematic;
import com.elevatemc.potpvp.match.MatchHandler;
import com.elevatemc.elib.menu.Button;
import com.elevatemc.elib.menu.Menu;
import com.elevatemc.elib.util.Callback;
import com.google.common.collect.ImmutableList;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.*;
import java.util.stream.Collectors;

public class SelectArenaMenu extends Menu {
    
    private final GameMode gameMode;
    private final Callback<String> callback;
    private final ArenaTeamfightCategory arenaTeamfightCategory;
    List<ArenaSchematic> possibleSchematics = new ArrayList<>();
    
    public SelectArenaMenu(GameMode gameMode, Callback<String> callback) {
        this(gameMode, null, callback);
    }

    public SelectArenaMenu(GameMode gameMode, ArenaTeamfightCategory arenaTeamfightCategory, Callback<String> callback) {
        this.callback = Preconditions.checkNotNull(callback, "callback");
        this.gameMode = Preconditions.checkNotNull(gameMode, "gameMode");
        this.arenaTeamfightCategory = arenaTeamfightCategory;

        for (ArenaSchematic schematic : PotPvPSI.getInstance().getArenaHandler().getSchematics()) {
            if (isValidArena(schematic)) {
                possibleSchematics.add(schematic);
            }
        }

        setPlaceholder(true);
    }

    private boolean isValidArena (ArenaSchematic schematic) {
        return schematic.isEnabled() && schematic.getEnabledGameModes().get(gameMode.getId());
    }

    @Override
    public String getTitle(Player player) {
        return ChatColor.BLUE + "Select an arena";
    }

    @Override
    public Map<Integer, Button> getButtons(Player arg0) {
        Map<Integer, Button> buttons = Maps.newHashMap();

        int i = 9;
        if (arenaTeamfightCategory == null) {
            buttons.put(0, new Button() {
                @Override
                public String getName(Player player) {
                    return ChatColor.GREEN + "Random Arena";
                }

                @Override
                public List<String> getDescription(Player player) {
                    return ImmutableList.of(
                            ChatColor.GRAY + "Click this to duel a random arena"
                    );
                }

                @Override
                public Material getMaterial(Player player) {
                    return Material.NETHER_STAR;
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType) {
                    callback.callback(null);
                }
            });
            for (ArenaSchematic schematic : possibleSchematics) {
                if ((i + 1) % 9 == 0) i++;
                if (i % 9 == 0) i++;
                buttons.put(i++, new ArenaButton(schematic, callback));
            }
        } else {
            for (ArenaSchematic schematic : getFilteredSchematics()) {
                if ((i + 1) % 9 == 0) i++;
                if (i % 9 == 0) i++;
                buttons.put(i++, new ArenaButton(schematic, callback));
            }
        }

        return buttons;
    }

    @Override
    public int size(Player player) {
        Map<Integer, Button> buttons = getButtons(player);
        return ((int)Math.ceil(((float)buttons.size() / 7)) + 2) * 9;
    }

    private List<ArenaSchematic> getFilteredSchematics() {
        switch (arenaTeamfightCategory) {
            case CITADELS:
                return possibleSchematics.stream().filter(schematic -> schematic.getName().startsWith("Citadel")).collect(Collectors.toList());
            case NETHER:
                return possibleSchematics.stream().filter(schematic -> schematic.getName().startsWith("Nether")).collect(Collectors.toList());
            case KOTHS:
                return possibleSchematics.stream().filter(schematic -> schematic.getName().startsWith("KOTH")).collect(Collectors.toList());
            default:
                return possibleSchematics.stream().filter(schematic -> !schematic.getName().startsWith("Citadel") && !schematic.getName().startsWith("KOTH") && !schematic.getName().startsWith("Nether")).collect(Collectors.toList());
        }
    }
}

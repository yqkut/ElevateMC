package com.elevatemc.potpvp.arena;

import com.elevatemc.potpvp.events.EventHandler;
import com.elevatemc.potpvp.events.event.GameEvent;
import com.elevatemc.potpvp.gamemode.GameMode;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.sk89q.worldedit.Vector;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;

import java.io.File;
import java.util.Map;

/**
 * Represents an arena schematic. See {@link com.elevatemc.potpvp.arena}
 * for a comparision of {@link Arena}s and {@link ArenaSchematic}s.
 */
public final class ArenaSchematic {

    /**
     * Name of this schematic (ex "Candyland")
     */
    @Getter private String name;

    /**
     * Display name of this Arena, will be used when communicating a GameMode
     * to players. Ex: "Map 15", "Ice KOTH", ...
     */
    @Getter @Setter private String displayName;

    /**
     * If matches can be scheduled on an instance of this arena.
     * Only impacts match scheduling, admin commands are (ignoring visual differences) nonchanged
     */
    @Setter private boolean enabled = false;

    /**
     * Material info which will be used when rendering this
     * arena in selection menus and such.
     */
    @Getter @Setter private Material icon;

    @Getter @Setter private boolean supportsRanked = true;
    @Getter @Setter private boolean pearlsAllowed = true;

    @Getter @Setter private Map<String, Boolean> enabledGameModes = Maps.newHashMap();

    @Getter @Setter private String eventName = null;

    /**
     * Index on the X axis on the grid (and in calculations regarding model arenas)
     * @see ArenaGrid
     */
    @Getter @Setter private int gridIndex;

    public ArenaSchematic() {} // for gson

    public ArenaSchematic(String name, String displayName) {
        this.name = Preconditions.checkNotNull(name, "name");
        this.displayName = Preconditions.checkNotNull(displayName, "displayName");
        this.icon = Material.GRASS;
    }

    public File getSchematicFile() {
        return new File(ArenaHandler.WORLD_EDIT_SCHEMATICS_FOLDER, name + ".schematic");
    }

    public Vector getModelArenaLocation() {
        int xModifier = ArenaGrid.GRID_SPACING_X * gridIndex;

        return new Vector(
            ArenaGrid.STARTING_POINT.getBlockX() - xModifier,
            ArenaGrid.STARTING_POINT.getBlockY(),
            ArenaGrid.STARTING_POINT.getBlockZ()
        );
    }

    public void pasteModelArena() throws Exception {
        Vector start = getModelArenaLocation();
        WorldEditUtils.paste(this, start);
    }

    public void removeModelArena() throws Exception {
        Vector start = getModelArenaLocation();
        Vector size = WorldEditUtils.readSchematicSize(this);

        WorldEditUtils.clear(
            start,
            start.add(size)
        );
    }

    public GameEvent getEvent() {
        if (eventName != null) {
            for (GameEvent event : EventHandler.EVENTS) {
                if (event.getName().equalsIgnoreCase(eventName)) {
                    return event;
                }
            }

            eventName = null;
        }

        return null;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof ArenaSchematic && ((ArenaSchematic) o).name.equals(name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public boolean isEnabled() {
        return enabled;
    }
}
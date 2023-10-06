package com.elevatemc.potpvp.arena;

import lombok.Getter;
import org.bukkit.Color;
import org.bukkit.Material;

public enum ArenaTeamfightCategory {
    CITADELS("Citadels", "In this category you can find a lot of citadels including HCTeams & Lunar citadels", Material.SMOOTH_BRICK),
    NETHER("Nether Maps", "In this category you can all maps related to nether", Material.NETHER_FENCE),
    KOTHS("KOTHs", "In this category you can find KOTHs", Material.LADDER),
    OTHER("Other", "In this category you can find all the other maps", Material.GRASS);

    @Getter private final String name;
    @Getter private final String description;
    @Getter private final Material icon;

    ArenaTeamfightCategory(String name, String description, Material icon) {
        this.name = name;
        this.description = description;
        this.icon = icon;
    }
}

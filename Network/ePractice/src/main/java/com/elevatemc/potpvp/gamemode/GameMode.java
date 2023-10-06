package com.elevatemc.potpvp.gamemode;

import com.elevatemc.potpvp.gamemode.kit.GameModeKit;
import lombok.Getter;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class GameMode {
    public abstract String getName();
    public abstract String getDescription();
    public abstract MaterialData getIcon();
    public abstract HealingMethod getHealingMethod();
    public abstract List<GameModeKit> getKits();
    public abstract boolean getBuildingAllowed();
    public abstract boolean getHealthShown();
    public abstract boolean getHardcoreHealing();
    public abstract boolean getPearlDamage();
    public abstract boolean getSupportsCompetitive();

    public String getId() {
        return getName().toUpperCase().replaceAll(" ", "_");
    }

    public boolean isVoidTeleport() {
        return true;
    }

    @Getter
    private static final ArrayList<GameMode> all = new ArrayList<>();
}

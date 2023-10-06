package com.elevatemc.potpvp.gamemodes.bridges;

import com.elevatemc.potpvp.gamemode.GameMode;
import com.elevatemc.potpvp.gamemode.HealingMethod;
import com.elevatemc.potpvp.gamemode.kit.GameModeKit;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;

import java.util.Collections;
import java.util.List;

public class Bridges extends GameMode {
    private final GameModeKit kit = GameModeKit.createFromGameMode(this);

    @Override
    public String getName() {
        return "Bridges";
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public MaterialData getIcon() {
        return new MaterialData(Material.CLAY, (byte) 5);
    }

    @Override
    public HealingMethod getHealingMethod() {
        return null;
    }

    @Override
    public List<GameModeKit> getKits() {
        return Collections.singletonList(kit);
    }

    @Override
    public boolean getBuildingAllowed() {
        return true;
    }

    @Override
    public boolean getHealthShown() {
        return false;
    }

    @Override
    public boolean getHardcoreHealing() {
        return false;
    }

    @Override
    public boolean getPearlDamage() {
        return false;
    }

    @Override
    public boolean getSupportsCompetitive() {
        return true;
    }
}

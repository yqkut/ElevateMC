package com.elevatemc.potpvp.gamemodes.archer;

import com.elevatemc.potpvp.gamemode.GameMode;
import com.elevatemc.potpvp.gamemode.HealingMethod;
import com.elevatemc.potpvp.gamemode.kit.GameModeKit;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;

import java.util.Collections;
import java.util.List;

public class Archer extends GameMode {
    private final GameModeKit kit = GameModeKit.createFromGameMode(this);

    @Override
    public String getName() {
        return "Archer";
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public MaterialData getIcon() {
        return new MaterialData(Material.BOW);
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
        return false;
    }

    @Override
    public boolean getHealthShown() {
        return true;
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

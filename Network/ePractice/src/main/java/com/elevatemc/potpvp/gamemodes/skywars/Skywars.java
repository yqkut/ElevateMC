package com.elevatemc.potpvp.gamemodes.skywars;

import com.elevatemc.potpvp.gamemode.GameMode;
import com.elevatemc.potpvp.gamemode.HealingMethod;
import com.elevatemc.potpvp.gamemode.kit.GameModeKit;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;

import java.util.Collections;
import java.util.List;

public class Skywars extends GameMode {
    private GameModeKit kit = GameModeKit.createFromGameMode(this);

    @Override
    public String getName() {
        return "Skywars";
    }

    @Override
    public String getDescription() {
        return "TODO WRITE DESCRIPTION"; // TODO: Write description
    }

    @Override
    public MaterialData getIcon() {
        return new MaterialData(Material.EGG);
    }

    @Override
    public HealingMethod getHealingMethod() {
        return HealingMethod.GOLDEN_APPLE;
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

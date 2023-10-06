package com.elevatemc.potpvp.gamemodes.sumo;

import com.elevatemc.potpvp.gamemode.GameMode;
import com.elevatemc.potpvp.gamemode.HealingMethod;
import com.elevatemc.potpvp.gamemode.kit.GameModeKit;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;

import java.util.Collections;
import java.util.List;

public class Sumo extends GameMode {
    @Override
    public String getName() {
        return "Sumo";
    }

    public String getDescription() {
        return "TODO WRITE DESCRIPTION"; // TODO: Write description
    }

    @Override
    public MaterialData getIcon() {
        return new MaterialData(Material.LEASH);
    }

    @Override
    public HealingMethod getHealingMethod() {
        return null;
    }

    @Override
    public List<GameModeKit> getKits() {
        return Collections.emptyList();
    }

    @Override
    public boolean getBuildingAllowed() {
        return false;
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

    @Override
    public boolean isVoidTeleport() {
        return false;
    }
}

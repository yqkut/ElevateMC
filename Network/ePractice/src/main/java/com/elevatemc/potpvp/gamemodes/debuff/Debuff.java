package com.elevatemc.potpvp.gamemodes.debuff;

import com.elevatemc.potpvp.gamemode.GameMode;
import com.elevatemc.potpvp.gamemode.HealingMethod;
import com.elevatemc.potpvp.gamemode.kit.GameModeKit;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;

import java.util.Collections;
import java.util.List;

public class Debuff extends GameMode {
    private final GameModeKit kit = GameModeKit.createFromGameMode(this);
    @Override
    public String getName() {
        return "Debuff";
    }

    @Override
    public String getDescription() {
        return "TODO WRITE DESCRIPTION"; // TODO: Write description
    }

    @Override
    public MaterialData getIcon() {
        return new MaterialData(Material.POTION, (byte) 4);
    }

    @Override
    public HealingMethod getHealingMethod() {
        return HealingMethod.POTIONS;
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
        return false;
    }

    @Override
    public boolean getHardcoreHealing() {
        return false;
    }

    @Override
    public boolean getPearlDamage() {
        return true;
    }

    @Override
    public boolean getSupportsCompetitive() {
        return true;
    }
}

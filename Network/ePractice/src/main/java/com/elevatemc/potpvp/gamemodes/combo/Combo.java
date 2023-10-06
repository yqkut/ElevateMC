package com.elevatemc.potpvp.gamemodes.combo;

import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.gamemode.GameMode;
import com.elevatemc.potpvp.gamemode.HealingMethod;
import com.elevatemc.potpvp.gamemode.kit.GameModeKit;
import com.elevatemc.potpvp.gamemodes.combo.listener.MatchComboListener;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;

import java.util.Collections;
import java.util.List;

public class Combo extends GameMode {
    private final GameModeKit kit = GameModeKit.createFromGameMode(this);

    public Combo() {
        Bukkit.getPluginManager().registerEvents(new MatchComboListener(), PotPvPSI.getInstance());
    }
    @Override
    public String getName() {
        return "Combo";
    }

    @Override
    public String getDescription() {
        return "TODO WRITE DESCRIPTION"; // TODO: Write description
    }

    @Override
    public MaterialData getIcon() {
        return new MaterialData(Material.RAW_FISH, (byte) 3);
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


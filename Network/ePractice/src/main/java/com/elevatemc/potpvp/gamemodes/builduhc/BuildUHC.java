package com.elevatemc.potpvp.gamemodes.builduhc;

import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.gamemode.GameMode;
import com.elevatemc.potpvp.gamemode.HealingMethod;
import com.elevatemc.potpvp.gamemode.kit.GameModeKit;
import com.elevatemc.potpvp.gamemodes.builduhc.listener.GoldenHeadListener;
import com.elevatemc.potpvp.gamemodes.builduhc.listener.MatchRodListener;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;

import java.util.Collections;
import java.util.List;

public class BuildUHC extends GameMode {
    private final GameModeKit kit = GameModeKit.createFromGameMode(this);

    public BuildUHC() {
        Bukkit.getPluginManager().registerEvents(new MatchRodListener(), PotPvPSI.getInstance());
        Bukkit.getPluginManager().registerEvents(new GoldenHeadListener(), PotPvPSI.getInstance());

    }
    @Override
    public String getName() {
        return "Build UHC";
    }

    @Override
    public String getDescription() {
        return "TODO WRITE DESCRIPTION"; // TODO: Write description
    }

    @Override
    public MaterialData getIcon() {
        return new MaterialData(Material.LAVA_BUCKET);
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

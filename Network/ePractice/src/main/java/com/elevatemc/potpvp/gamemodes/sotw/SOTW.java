package com.elevatemc.potpvp.gamemodes.sotw;

import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.gamemode.GameMode;
import com.elevatemc.potpvp.gamemode.HealingMethod;
import com.elevatemc.potpvp.gamemode.kit.GameModeKit;
import com.elevatemc.potpvp.gamemodes.pearlfight.listener.MatchPearlfightListener;
import com.elevatemc.potpvp.gamemodes.sotw.listener.MatchSOTWListener;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;

import java.util.Collections;
import java.util.List;

public class SOTW extends GameMode {
    public SOTW() {
        Bukkit.getPluginManager().registerEvents(new MatchSOTWListener(), PotPvPSI.getInstance());
    }
    @Override
    public String getName() {
        return "SOTW";
    }

    @Override
    public String getDescription() {
        return "TODO WRITE DESCRIPTION"; // TODO: Write description
    }

    @Override
    public MaterialData getIcon() {
        return new MaterialData(Material.IRON_CHESTPLATE);
    }

    @Override
    public HealingMethod getHealingMethod() {
        return HealingMethod.POTIONS;
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
}

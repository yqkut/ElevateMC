package com.elevatemc.potpvp.gamemodes.bedfight;

import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.gamemode.GameMode;
import com.elevatemc.potpvp.gamemode.HealingMethod;
import com.elevatemc.potpvp.gamemode.kit.GameModeKit;
import com.elevatemc.potpvp.gamemodes.bedfight.listener.BedFightListener;
import com.elevatemc.potpvp.gamemodes.pearlfight.listener.MatchPearlfightListener;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;

import java.util.Collections;
import java.util.List;

public class BedFight extends GameMode {
    private GameModeKit kit = GameModeKit.createFromGameMode(this);

    public BedFight() {
        Bukkit.getPluginManager().registerEvents(new BedFightListener(), PotPvPSI.getInstance());
    }
    @Override
    public String getName() {
        return "Bed Fight";
    }

    @Override
    public String getDescription() {
        return "TODO WRITE DESCRIPTION"; // TODO: Write description
    }

    @Override
    public MaterialData getIcon() {
        return new MaterialData(Material.BED);
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

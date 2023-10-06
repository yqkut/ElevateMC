package com.elevatemc.potpvp.gamemodes.trapping;

import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.gamemode.GameMode;
import com.elevatemc.potpvp.gamemode.HealingMethod;
import com.elevatemc.potpvp.gamemode.kit.GameModeKit;
import com.elevatemc.potpvp.gamemodes.sotw.listener.MatchSOTWListener;
import com.elevatemc.potpvp.gamemodes.trapping.listener.MatchTrappingListener;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Trapping extends GameMode {
    public Trapping() {
        Bukkit.getPluginManager().registerEvents(new MatchTrappingListener(), PotPvPSI.getInstance());
    }
    public final GameModeKit RUNNER = new GameModeKit("HCF_RUN", "Runner", new MaterialData(Material.DIAMOND_SWORD));
    public final GameModeKit TRAPPER = new GameModeKit("HCF_TRAP", "Trapper", new MaterialData(Material.IRON_PICKAXE));

    @Override
    public String getName() {
        return "Trapping";
    }

    @Override
    public String getDescription() {
        return "TODO WRITE DESCRIPTION"; // TODO: Write description
    }

    @Override
    public MaterialData getIcon() {
        return new MaterialData(Material.MAGMA_CREAM);
    }

    @Override
    public HealingMethod getHealingMethod() {
        return HealingMethod.POTIONS;
    }

    @Override
    public List<GameModeKit> getKits() {
        return Arrays.asList(RUNNER, TRAPPER);
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
        return true;
    }

    @Override
    public boolean getSupportsCompetitive() {
        return false;
    }
}

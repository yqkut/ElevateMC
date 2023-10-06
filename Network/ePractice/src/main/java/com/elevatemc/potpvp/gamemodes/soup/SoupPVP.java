package com.elevatemc.potpvp.gamemodes.soup;


import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.gamemode.GameMode;
import com.elevatemc.potpvp.gamemode.HealingMethod;
import com.elevatemc.potpvp.gamemode.kit.GameModeKit;
import com.elevatemc.potpvp.gamemodes.soup.listener.MatchSoupListener;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;

import java.util.Collections;
import java.util.List;

public class SoupPVP extends GameMode {
    private final GameModeKit kit = GameModeKit.createFromGameMode(this);

    public SoupPVP() {
        Bukkit.getPluginManager().registerEvents(new MatchSoupListener(), PotPvPSI.getInstance());
    }

    @Override
    public String getName() {
        return "Soup";
    }

    @Override
    public String getDescription() {
        return "TODO WRITE DESCRIPTION"; // TODO: Write description
    }

    @Override
    public MaterialData getIcon() {
        return new MaterialData(Material.MUSHROOM_SOUP);
    }

    @Override
    public HealingMethod getHealingMethod() {
        return HealingMethod.SOUP;
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
        return false;
    }

    @Override
    public boolean getSupportsCompetitive() {
        return true;
    }
}


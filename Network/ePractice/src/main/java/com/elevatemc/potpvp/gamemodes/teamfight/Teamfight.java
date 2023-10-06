package com.elevatemc.potpvp.gamemodes.teamfight;

import com.elevatemc.potpvp.gamemode.GameMode;
import com.elevatemc.potpvp.gamemode.HealingMethod;
import com.elevatemc.potpvp.gamemode.kit.GameModeKit;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;

import java.util.Arrays;
import java.util.List;

public class Teamfight extends GameMode {
    public final GameModeKit DIAMOND_HCF = new GameModeKit("DIAMOND_HCF", "Diamond", new MaterialData(Material.DIAMOND_CHESTPLATE));
    public final GameModeKit BARD_HCF = new GameModeKit("BARD_HCF", "Bard", new MaterialData(Material.GOLD_CHESTPLATE));
    public final GameModeKit ARCHER_HCF = new GameModeKit("ARCHER_HCF", "Archer", new MaterialData(Material.LEATHER_CHESTPLATE));
    public final GameModeKit ROGUE_HCF = new GameModeKit("ROGUE_HCF", "Rogue", new MaterialData(Material.CHAINMAIL_CHESTPLATE));

    @Override
    public String getName() {
        return "Teamfight";
    }

    @Override
    public String getDescription() {
        return "TODO WRITE DESCRIPTION"; // TODO: Write description
    }

    @Override
    public MaterialData getIcon() {
        return new MaterialData(Material.GOLD_NUGGET);
    }

    @Override
    public HealingMethod getHealingMethod() {
        return HealingMethod.POTIONS;
    }

    @Override
    public List<GameModeKit> getKits() {
        return Arrays.asList(DIAMOND_HCF, BARD_HCF, ARCHER_HCF, ROGUE_HCF);
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
        return false;
    }
}

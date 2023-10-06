package com.elevatemc.potpvp.gamemodes.teamfight;

import com.elevatemc.potpvp.gamemode.kit.GameModeKit;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;

import java.util.Arrays;
import java.util.List;

public class TeamfightDebuff extends Teamfight {
    public final GameModeKit DEBUFF_DIAMOND_HCF = new GameModeKit("DEBUFF_DIAMOND_HCF", "Diamond", new MaterialData(Material.DIAMOND_CHESTPLATE));
    public final GameModeKit DEBUFF_BARD_HCF = new GameModeKit("DEBUFF_BARD_HCF", "Bard", new MaterialData(Material.GOLD_CHESTPLATE));
    public final GameModeKit DEBUFF_ARCHER_HCF = new GameModeKit("DEBUFF_ARCHER_HCF", "Archer", new MaterialData(Material.LEATHER_CHESTPLATE));
    public final GameModeKit DEBUFF_ROGUE_HCF = new GameModeKit("DEBUFF_ROGUE_HCF", "Rogue", new MaterialData(Material.CHAINMAIL_CHESTPLATE));

    @Override
    public String getName() {
        return "Teamfight Debuff";
    }

    @Override
    public String getDescription() {
        return "TODO WRITE DESCRIPTION"; // TODO: Write description
    }

    @Override
    public List<GameModeKit> getKits() {
        return Arrays.asList(DEBUFF_DIAMOND_HCF, DEBUFF_BARD_HCF, DEBUFF_ARCHER_HCF, DEBUFF_ROGUE_HCF);
    }
}

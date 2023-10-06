package com.elevatemc.potpvp.pvpclasses;

import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.gamemode.GameModes;
import com.elevatemc.potpvp.hctranked.game.RankedGameTeam;
import com.elevatemc.potpvp.tournament.Tournament;
import lombok.Getter;
import com.elevatemc.potpvp.party.Party;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;

public enum PvPClasses {
    DIAMOND(Material.DIAMOND_CHESTPLATE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE),
    BARD(Material.GOLD_CHESTPLATE, 2, 2, 4),
    ARCHER(Material.LEATHER_CHESTPLATE, 2, 2, 4),
    ROGUE(Material.CHAINMAIL_CHESTPLATE, 2, 2, 4);

    @Getter private final Material icon;
    @Getter private final int maxForFive;
    @Getter private final int maxForTen;
    @Getter private final int maxForTwenty;

    PvPClasses(Material icon, int maxForFive, int maxForTen, int maxForTwenty) {
        this.icon = icon;
        this.maxForFive = maxForFive;
        this.maxForTen = maxForTen;
        this.maxForTwenty = maxForTwenty;
    }

    public boolean allowed(Party party) {
        if (this.equals(PvPClasses.DIAMOND)) {
            return true;
        }

        int current = (int) party.getKits().values().stream().filter(pvPClasses -> pvPClasses == this).count();
        int size = party.getMembers().size();

        Tournament tournament = PotPvPSI.getInstance().getTournamentHandler().getTournament();
        if (tournament != null && tournament.isInTournament(party)) {
            if (!tournament.getType().equals(GameModes.TEAMFIGHT) || !tournament.getType().equals(GameModes.TEAMFIGHT_DEBUFF)) return false;
            if (this.equals(PvPClasses.ROGUE)) {
                return false;
            }
            if (this.equals(PvPClasses.BARD) && current < tournament.getBards()) {
                return true;
            }
            if (this.equals(PvPClasses.ARCHER) && current < tournament.getArchers()) {
                return true;
            }
            return false;
        } else {
            if (size < 10 && current >= maxForFive) {
                return false;
            }

            if (size < 20 && current >= maxForTen) {
                return false;
            }

            return current < maxForTwenty;
        }
    }

    public boolean allowed(RankedGameTeam team) {
        if (this.equals(PvPClasses.DIAMOND)) {
            return true;
        }

        int current = (int) team.getKits().values().stream().filter(pvPClasses -> pvPClasses == this).count();

        return current < 1;
    }

    public String getName() {
        return StringUtils.capitalize(this.name().toLowerCase());
    }

}

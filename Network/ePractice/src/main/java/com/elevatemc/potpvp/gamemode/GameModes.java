package com.elevatemc.potpvp.gamemode;

import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.arena.ArenaSchematic;
import com.elevatemc.potpvp.gamemodes.archer.Archer;
import com.elevatemc.potpvp.gamemodes.bedfight.BedFight;
import com.elevatemc.potpvp.gamemodes.boxing.Boxing;
import com.elevatemc.potpvp.gamemodes.bridges.Bridges;
import com.elevatemc.potpvp.gamemodes.builduhc.BuildUHC;
import com.elevatemc.potpvp.gamemodes.combo.Combo;
import com.elevatemc.potpvp.gamemodes.debuff.Debuff;
import com.elevatemc.potpvp.gamemodes.gapple.Gapple;
import com.elevatemc.potpvp.gamemodes.invaded.Invaded;
import com.elevatemc.potpvp.gamemodes.nodebuff.NoDebuff;
import com.elevatemc.potpvp.gamemodes.pearlfight.PearlFight;
import com.elevatemc.potpvp.gamemodes.skywars.Skywars;
import com.elevatemc.potpvp.gamemodes.sotw.SOTW;
import com.elevatemc.potpvp.gamemodes.soup.SoupPVP;
import com.elevatemc.potpvp.gamemodes.spleef.Spleef;
import com.elevatemc.potpvp.gamemodes.sumo.Sumo;
import com.elevatemc.potpvp.gamemodes.teamfight.Teamfight;
import com.elevatemc.potpvp.gamemodes.teamfight.TeamfightDebuff;
import com.elevatemc.potpvp.gamemodes.trapping.Trapping;
import com.elevatemc.potpvp.gamemodes.vanilla.Vanilla;

public class GameModes {
    public static final NoDebuff NO_DEBUFF = new NoDebuff();
    public static final Debuff DEBUFF = new Debuff();
    public static final Archer ARCHER = new Archer();
    public static final Boxing BOXING = new Boxing();
    public static final BedFight BED_FIGHT = new BedFight();
    public static final Gapple GAPPLE = new Gapple();
    public static final Invaded INVADED = new Invaded();
    public static final Skywars SKYWARS = new Skywars();
    public static final SOTW SOTW = new SOTW();
    public static final BuildUHC BUILD_UHC = new BuildUHC();

    public static final SoupPVP SOUP_PVP = new SoupPVP();
    public static final Combo COMBO = new Combo();
    public static final Spleef SPLEEF = new Spleef();
    public static final Sumo SUMO = new Sumo();
    public static final Teamfight TEAMFIGHT = new Teamfight();
    public static final TeamfightDebuff TEAMFIGHT_DEBUFF = new TeamfightDebuff();
    public static final Trapping TRAPPING = new Trapping();
    public static final Vanilla VANILLA = new Vanilla();
    public static final Bridges BRIDGES = new Bridges();
    public static final PearlFight PEARL_FIGHT = new PearlFight();

    public static void loadGameModes() {
        GameMode.getAll().add(NO_DEBUFF);
        GameMode.getAll().add(DEBUFF);
        GameMode.getAll().add(TRAPPING);
        GameMode.getAll().add(PEARL_FIGHT);
        GameMode.getAll().add(BED_FIGHT);
        GameMode.getAll().add(BOXING);
//        GameMode.getAll().add(BRIDGES);
        GameMode.getAll().add(SUMO);
        GameMode.getAll().add(INVADED);
        GameMode.getAll().add(GAPPLE);
        GameMode.getAll().add(VANILLA);
        GameMode.getAll().add(SOTW);
        GameMode.getAll().add(SKYWARS);
        GameMode.getAll().add(SPLEEF);
        GameMode.getAll().add(TEAMFIGHT);
        GameMode.getAll().add(TEAMFIGHT_DEBUFF);
        GameMode.getAll().add(BUILD_UHC);
//        GameMode.getAll().add(COMBO);
        GameMode.getAll().add(SOUP_PVP);

        GameMode.getAll().forEach(PotPvPSI.getInstance().getQueueHandler()::addQueues);
        System.out.println("All game-modes were loaded!");
    }
}

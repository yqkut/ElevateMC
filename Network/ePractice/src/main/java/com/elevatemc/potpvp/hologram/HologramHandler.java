package com.elevatemc.potpvp.hologram;

import com.elevatemc.elib.eLib;
import com.elevatemc.elib.hologram.Hologram;
import com.elevatemc.elib.util.TaskUtil;
import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.gamemode.GameMode;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class HologramHandler {

    World world = Bukkit.getWorld("world");

    public void registerWelcomeHologram() {
        if(world == null) return;
        Hologram welcomeHologram = new Hologram(new Location(world, 5, 111, 5));
        welcomeHologram.addLine("&3&lElevateMC");
        welcomeHologram.addLine("&r");
        welcomeHologram.addLine("Season 3 of &3&lElevateMC &ftook place on the 6th of August, 2022");
        welcomeHologram.addLine("Home of &3&lHCT Ranked");
        welcomeHologram.addLine("&r");
        welcomeHologram.addLine("&3Website: &felevatemc.com");
        welcomeHologram.addLine("&3Store: &fstore.elevatemc.com");
        welcomeHologram.addLine("&3Discord: &felevatemc.com/discord");
        eLib.getInstance().getHologramHandler().registerHologram(welcomeHologram);
    }

    private int currentLeaderboard = 0;

    public void registerLeaderboardHologram() {
        if(world == null) return;
        Hologram leaderBoardHologram = new Hologram(new Location(world, 21, 109, 25));
        leaderBoardHologram.addLine("&3&lRanked");
        leaderBoardHologram.addLine("&3&lLeaderboards");
        leaderBoardHologram.addLine("&f");
        leaderBoardHologram.addLine("&7Loading...");
        leaderBoardHologram.addLine("&f");
        leaderBoardHologram.addLine("&7");
        leaderBoardHologram.addLine("&7");
        leaderBoardHologram.addLine("&7");
        leaderBoardHologram.addLine("&7");
        leaderBoardHologram.addLine("&7");
        leaderBoardHologram.addLine("&7");
        leaderBoardHologram.addLine("&7");
        leaderBoardHologram.addLine("&7");
        leaderBoardHologram.addLine("&7");
        leaderBoardHologram.addLine("&7");
        eLib.getInstance().getHologramHandler().registerHologram(leaderBoardHologram);
        TaskUtil.scheduleAtFixedRateOnPool(() -> updateConsumer.accept(leaderBoardHologram), 5, 5, TimeUnit.SECONDS);
    }

    private final Consumer<Hologram> updateConsumer = hologram -> {
        List<GameMode> leaderboardGames = GameMode.getAll().stream().filter(GameMode::getSupportsCompetitive).collect(Collectors.toList());
        GameMode gameMode = leaderboardGames.get(currentLeaderboard);
        if (hologram.getLines().size() >= 3) {
            hologram.getLine(3).updateLine(ChatColor.GREEN + " • " + gameMode.getName() + " • ");
        } else {
            hologram.addLine(3, ChatColor.GREEN + " • " + gameMode.getName() + " • ");
        }

        int i = 5;

        for (Map.Entry<String, Integer> entry : PotPvPSI.getInstance().getEloHandler().topElo(gameMode).entrySet()) {
            if (hologram.getLines().size() >= i) {
                hologram.getLine(i).updateLine(ChatColor.GRAY.toString() + (i - 4) + ". " + entry.getKey() + ChatColor.GRAY + ": " + ChatColor.WHITE + entry.getValue());
            } else {
                hologram.addLine(i, ChatColor.GRAY.toString() + (i - 4) + ". " + entry.getKey() + ChatColor.GRAY + ": " + ChatColor.WHITE + entry.getValue());
            }


            i++;
        }

        currentLeaderboard++;
        if (currentLeaderboard > leaderboardGames.size() - 1) currentLeaderboard = 0;
    };

    public void registerHolograms() {
        registerWelcomeHologram();
        registerLeaderboardHologram();
    }
}

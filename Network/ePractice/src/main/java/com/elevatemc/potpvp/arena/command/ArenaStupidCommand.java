package com.elevatemc.potpvp.arena.command;

import com.elevatemc.elib.command.Command;
import com.elevatemc.elib.command.param.Parameter;
import com.elevatemc.elib.util.PlayerUtils;
import com.elevatemc.elib.util.TimeUtils;
import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.arena.Arena;
import com.elevatemc.potpvp.arena.ArenaHandler;
import com.elevatemc.potpvp.arena.ArenaSchematic;
import com.elevatemc.potpvp.arena.WorldEditUtils;
import com.elevatemc.potpvp.util.Color;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.github.paperspigot.Title;

import java.util.*;

public final class ArenaStupidCommand {
    @Command(names = { "arena stupid" }, permission = "op", description = "Stupid chunks - Recommended value for ticks is 20")
    public static void arenaScale(Player sender, @Parameter(name="schematic") ArenaSchematic schematic, @Parameter(name="ticks") int ticks) {
        ArenaHandler arenaHandler = PotPvPSI.getInstance().getArenaHandler();

        Set<Arena> arenas = arenaHandler.getArenas(schematic);
        if (arenas.size() < 1) {
            sender.sendMessage(ChatColor.RED + "There are no instances of this arena...");
            return;
        }
        List<Location> locations = new ArrayList<>();
        for (Arena arena : arenas) {
            locations.add(arena.getTeam1Spawn());
            locations.add(arena.getTeam2Spawn());
        }

        processLocations(sender, locations, ticks);
    }

    @Command(names = { "arena stupidall" }, permission = "op", description = "Stupid chunks - Recommended value for ticks is 20")
    public static void arenaScale(Player sender, @Parameter(name="ticks") int ticks) {
        ArenaHandler arenaHandler = PotPvPSI.getInstance().getArenaHandler();

        List<Location> locations = new ArrayList<>();
        for (ArenaSchematic schem: arenaHandler.getSchematics()) {
            for (Arena arena : arenaHandler.getArenas(schem)) {
                locations.add(arena.getTeam1Spawn());
                locations.add(arena.getTeam2Spawn());
            }
        }

        processLocations(sender, locations, ticks);
    }

    private final static Title.Builder loadingLocations = Title.builder().stay(2147483647).title(Color.translate("&3Loading Locations"));

    public static void processLocations(Player player, List<Location> locations, long ticks) {

        double estimated = ((ticks / 20) * locations.size()) + (locations.size() * 0.3);

        player.sendMessage(Color.translate("&3Location Amount: &b" + locations.size() +  " &3ETA: &b" + TimeUtils.formatIntoMMSS((int)estimated)));

        Iterator<Location> iterator = locations.iterator();

        new BukkitRunnable() {

            int done = 1;

            @Override
            public void run() {
                if (!player.isOnline()) {
                    cancel();
                    return;
                }

                if (!iterator.hasNext()) {
                    player.sendMessage(Color.translate("&aFinished."));
                    cancel();
                    return;
            }
                Location location = iterator.next();
                player.teleport(location);
                player.sendMessage(Color.translate("&3Location: " + "&b" + done + "/" + locations.size()));
                done++;
            }

        }.runTaskTimer(PotPvPSI.getInstance(), 20L, ticks);
    }
}
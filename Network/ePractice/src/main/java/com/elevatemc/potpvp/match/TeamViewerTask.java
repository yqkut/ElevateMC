package com.elevatemc.potpvp.match;

import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.match.event.MatchTerminateEvent;
import com.lunarclient.bukkitapi.LunarClientAPI;
import com.lunarclient.bukkitapi.nethandler.client.LCPacketTeammates;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class TeamViewerTask extends BukkitRunnable implements Listener {
    public TeamViewerTask() {
        PotPvPSI.getInstance().getServer().getPluginManager().registerEvents(this, PotPvPSI.getInstance());
    }

    @Override
    public void run() {
        for (Match match : PotPvPSI.getInstance().getMatchHandler().getHostedMatches()) {
            if (match.getState() != MatchState.TERMINATED) {
                List<MatchTeam> teams = match.getTeams();
                for (MatchTeam team : teams) {
                    // Populate the alive array
                    ArrayList<Player> alive = new ArrayList<>();
                    for (UUID u : team.getAliveMembers()) {
                        Player player = Bukkit.getPlayer(u);
                        if (player != null) alive.add(player);
                    }
                    // For each alive person we send the teammates
                    for (Player player : alive) {
                        sendTeammates(player, alive);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onMatchEnd(MatchTerminateEvent e) {
        List<MatchTeam> teams = e.getMatch().getTeams();
        for (MatchTeam team : teams) {
            for (UUID u : team.getAllMembers()) {
                Player player = Bukkit.getPlayer(u);
                if (player != null) LunarClientAPI.getInstance().sendTeammates(player, new LCPacketTeammates(null, 1, new HashMap<>()));
            }
        }
    }

    public void sendTeammates(Player player, List<Player> targets) {
        Map<UUID, Map<String, Double>> playerMap = new HashMap<>();

        for (Player target : targets) {
            Map<String, Double> posMap = new HashMap<>();

            posMap.put("x", target.getLocation().getX());
            posMap.put("y", target.getLocation().getY());
            posMap.put("z", target.getLocation().getZ());

            playerMap.put(target.getUniqueId(), posMap);
        }

        LunarClientAPI.getInstance().sendTeammates(player, new LCPacketTeammates(player.getUniqueId(), 1, playerMap));
    }
}


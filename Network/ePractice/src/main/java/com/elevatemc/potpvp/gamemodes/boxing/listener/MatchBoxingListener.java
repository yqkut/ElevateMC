package com.elevatemc.potpvp.gamemodes.boxing.listener;

import com.elevatemc.elib.util.ItemBuilder;
import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.gamemode.GameMode;
import com.elevatemc.potpvp.gamemode.GameModes;
import com.elevatemc.potpvp.match.Match;
import com.elevatemc.potpvp.match.MatchHandler;
import com.elevatemc.potpvp.match.MatchTeam;
import com.elevatemc.potpvp.match.event.MatchCountdownStartEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.UUID;

public final class MatchBoxingListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player damager = (Player) event.getEntity();
            Match match = PotPvPSI.getInstance().getMatchHandler().getMatchPlaying(damager);
            if (match != null && match.getGameMode().equals(GameModes.BOXING)) {
                List<MatchTeam> teams = match.getTeams();
                if (teams.size() > 2) {
                    int won = -1;
                    for (MatchTeam team : teams) {
                        if (team.getHits() == 100) {
                            won = teams.indexOf(team);
                        }
                    }
                    if (won != -1) {
                        for (MatchTeam team : teams) {
                            if (teams.indexOf(team) != won) {
                                for (UUID alive : team.getAliveMembers()) {
                                    Bukkit.getPlayer(alive).setHealth(0);
                                }
                            }
                        }
                    }
                } else {
                    MatchTeam firstTeam = teams.get(0);
                    MatchTeam secondTeam = teams.get(1);
                    if (firstTeam.getHits() == 100) {
                        for (UUID alive : secondTeam.getAliveMembers()) {
                            Bukkit.getPlayer(alive).setHealth(0);
                        }
                    } else if (secondTeam.getHits() == 100) {
                        for (UUID alive : firstTeam.getAliveMembers()) {
                            Bukkit.getPlayer(alive).setHealth(0);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (event.getEntityType() != EntityType.PLAYER) {
            return;
        }

        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        Match match = matchHandler.getMatchPlaying((Player) event.getEntity());

        if (match != null && match.getGameMode().equals(GameModes.BOXING)) {
            event.setFoodLevel(20);
        }
    }


    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntityType() != EntityType.PLAYER) {
            return;
        }

        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        Match match = matchHandler.getMatchPlaying((Player) event.getEntity());

        if (match != null && match.getGameMode().equals(GameModes.BOXING)) {
            event.setDamage(0);
        }
    }
}
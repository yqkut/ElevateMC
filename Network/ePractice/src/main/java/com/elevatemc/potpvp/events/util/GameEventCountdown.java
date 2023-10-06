package com.elevatemc.potpvp.events.util;

import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.events.util.team.GameTeam;
import com.elevatemc.potpvp.util.Color;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class GameEventCountdown {

    private int duration;
    private final Runnable runnable;
    private final List<GameTeam> participants;

    public GameEventCountdown(int duration, Runnable runnable, List<GameTeam> participants) {
        this.duration = duration;
        this.runnable = runnable;
        this.participants = participants;
        new Countdown().runTaskTimerAsynchronously(PotPvPSI.getInstance(), 0L, 20L);
    }

    private final class Countdown extends BukkitRunnable {

        @Override
        public void run() {
            if(duration == -1) {
                cancel();
                return;
            }

            for(GameTeam participant : participants) {
                for(Player player : participant.getPlayers()) {
                    if(duration > 0) {
                        player.sendMessage(Color.translate("&e" + duration + "..."));
                        player.playSound(player.getLocation(), Sound.NOTE_PLING, 1F, 1F);
                    } else {
                        player.sendMessage(Color.translate("&aMatch started."));
                        player.playSound(player.getLocation(), Sound.NOTE_PLING, 1F, 2F);
                    }
                }
            }

            if(duration == 0) {
                runnable.run();
            }

            duration--;
        }
    }
}

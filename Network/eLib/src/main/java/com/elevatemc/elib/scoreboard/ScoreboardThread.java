package com.elevatemc.elib.scoreboard;

import com.elevatemc.elib.eLib;
import org.bukkit.entity.Player;

final class ScoreboardThread extends Thread {

    public static Integer UPDATE_INTERVAL = 2;

    public ScoreboardThread() {
        super("eLib - Scoreboard Thread");
        setDaemon(false);
    }

    public void run() {
        while (true) {

            for (Player online : eLib.getInstance().getServer().getOnlinePlayers()) {

                try {
                    eLib.getInstance().getScoreboardHandler().updateScoreboard(online);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            try {
                Thread.sleep(UPDATE_INTERVAL * 50L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
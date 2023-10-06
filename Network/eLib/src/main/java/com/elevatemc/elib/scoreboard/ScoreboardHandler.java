package com.elevatemc.elib.scoreboard;

import com.elevatemc.elib.eLib;
import com.elevatemc.elib.scoreboard.config.ScoreboardConfiguration;
import com.elevatemc.elib.scoreboard.listener.ScoreboardListener;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ScoreboardHandler {

    @Getter private Map<String,Scoreboard> boards = new ConcurrentHashMap<>();
    @Getter @Setter private ScoreboardConfiguration configuration = null;

    public ScoreboardHandler() {
        new ScoreboardThread().start();
        eLib.getInstance().getServer().getPluginManager().registerEvents(new ScoreboardListener(), eLib.getInstance());
    }

    public void create(Player player) {
        if (configuration != null) {
            boards.put(player.getName(), new Scoreboard(player));
        }

    }

    public void updateScoreboard(Player player) {

        final Scoreboard board = boards.get(player.getName());

        if (board != null) {
            board.update();
        }

    }


    public void remove(Player player) {
        boards.remove(player.getName());
    }

}
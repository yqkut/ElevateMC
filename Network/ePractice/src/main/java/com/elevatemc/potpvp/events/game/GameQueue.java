package com.elevatemc.potpvp.events.game;

import com.elevatemc.elib.util.TaskUtil;
import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.events.task.GameStartTask;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

@Getter
public class GameQueue {

    private final List<Game> runningGames = new ArrayList<>();
    private final LinkedList<Game> games = new LinkedList<>();

    public static GameQueue INSTANCE;

    public GameQueue() {
        INSTANCE = this;
    }

    public void run(PotPvPSI plugin) {
        TaskUtil.runTaskTimer(() -> check(plugin), 20, 20);
    }

    private void check(PotPvPSI plugin) {
        final Game game = games.peek();
        if(game != null) {
            if(game.getState() == GameState.QUEUED) {
                int count = 0;
                boolean cancelled = false;

                for(Game other : runningGames) {
                    if(other.getState() == GameState.STARTING || other.getState() == GameState.ENDED) {
                        cancelled = true;
                        break;
                    }

                    if(other.getEvent() == game.getEvent()) {
                        count++;
                    }
                }

                if(count >= game.getEvent().getMaxInstances()) {
                    cancelled = true;
                }

                if(!game.getHost().isOnline()) {
                    games.remove();
                    cancelled = true;
                }

                if(!cancelled) {
                    games.remove();
                    runningGames.add(game);
                    new GameStartTask(plugin, game);
                }
            }
        }

        final Iterator<Game> iterator = runningGames.iterator();
        while(iterator.hasNext()) {
            final Game runningGame = iterator.next();

            if(runningGame.getState() == GameState.ENDED) {
                iterator.remove();
                continue;
            }

            int onlinePlayers = 0;
            for(Player player : runningGame.getPlayers()) {
                if(player.isOnline()) onlinePlayers++;
            }

            if(runningGame.getState() != GameState.STARTING && (runningGame.getPlayers().isEmpty() || onlinePlayers == 0)) {
                iterator.remove();
                game.end();
                continue;
            }
        }
    }

    public void add(Game game) {
        games.add(game);
    }

    public int size() {
        return games.size();
    }

    public List<Game> getCurrentGames() {
        return runningGames;
    }

    public Game getCurrentGame(Player player) {
        return runningGames.stream().filter(game -> game.getPlayers().contains(player)).findFirst().orElse(null);
    }
}

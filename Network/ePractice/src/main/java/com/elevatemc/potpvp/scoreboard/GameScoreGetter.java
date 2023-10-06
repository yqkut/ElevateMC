package com.elevatemc.potpvp.scoreboard;

import com.elevatemc.potpvp.events.game.Game;
import com.elevatemc.potpvp.events.game.GameQueue;
import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.function.BiConsumer;

public class GameScoreGetter implements BiConsumer<Player, LinkedList<String>> {

    @Override
    public void accept(Player player, LinkedList<String> scores) {
        final Game game = GameQueue.INSTANCE.getCurrentGame(player);

        if(game == null) return;
        if(!game.getPlayers().contains(player)) return;

        scores.addAll(game.getEvent().getScoreboardScores(player, game));
    }
}

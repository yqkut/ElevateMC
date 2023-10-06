package com.elevatemc.potpvp.events.bukkit.event;

import com.elevatemc.elib.util.TaskUtil;
import com.elevatemc.potpvp.events.game.Game;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public class PlayerQuitGameEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final Game game;

    public PlayerQuitGameEvent(Player player, Game game) {
        this.player = player;
        this.game = game;

        TaskUtil.runTaskLater(() -> {
            game.getPlayers().remove(player);
            game.getSpectators().remove(player);
        }, 2);
    }
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}

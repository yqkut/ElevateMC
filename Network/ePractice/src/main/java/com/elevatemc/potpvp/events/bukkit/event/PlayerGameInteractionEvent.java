package com.elevatemc.potpvp.events.bukkit.event;

import com.elevatemc.potpvp.events.game.Game;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@RequiredArgsConstructor
public class PlayerGameInteractionEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final Game game;

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}

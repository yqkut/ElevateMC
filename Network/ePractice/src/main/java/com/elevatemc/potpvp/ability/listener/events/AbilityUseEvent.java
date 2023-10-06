package com.elevatemc.potpvp.ability.listener.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import com.elevatemc.potpvp.ability.Ability;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@AllArgsConstructor
@Getter
@Setter
public class AbilityUseEvent extends Event implements Cancellable {
    @Getter private static final HandlerList handlerList = new HandlerList();

    private final Player player;
    private final Player target;
    private final Location chosenLocation;
    private final Ability ability;
    private boolean cancelled;

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}

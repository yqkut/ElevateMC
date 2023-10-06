package com.elevatemc.potpvp.events;

import com.elevatemc.elib.util.ItemBuilder;
import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.events.event.GameEvent;
import com.elevatemc.potpvp.events.event.impl.brackets.BracketsGameEvent;
import com.elevatemc.potpvp.events.event.impl.lms.LastManStandingGameEvent;
import com.elevatemc.potpvp.events.event.impl.sumo.SumoGameEvent;
import com.elevatemc.potpvp.events.game.GameQueue;
import com.elevatemc.potpvp.util.Color;
import com.google.common.collect.ImmutableList;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@Getter
public class EventHandler {

    private final GameQueue gameQueue;

    public EventHandler(PotPvPSI plugin) {
        this.gameQueue = new GameQueue();
        gameQueue.run(plugin);
    }

    public static final List<GameEvent> EVENTS = ImmutableList.of(
            new LastManStandingGameEvent(),
            new BracketsGameEvent(),
            new SumoGameEvent()
    );

    public static ItemStack getLeaveItem() {
        return ItemBuilder.of(Material.INK_SACK).data((short)1).name(Color.translate("&cLeave Event")).build();
    }
}

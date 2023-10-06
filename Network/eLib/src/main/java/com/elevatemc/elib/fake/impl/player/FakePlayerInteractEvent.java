package com.elevatemc.elib.fake.impl.player;

import com.elevatemc.elib.util.event.PlayerEvent;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

/**
 * @author ImHacking
 */

@Getter
public class FakePlayerInteractEvent extends PlayerEvent {

    private final String fakePlayerName;

    @Setter
    private String command;

    public FakePlayerInteractEvent(Player player, String fakePlayerName, String command) {
        super(player);
        this.fakePlayerName = fakePlayerName;
        this.command = command;
    }
}

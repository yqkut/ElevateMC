package com.elevatemc.elib.border.event.player;

import lombok.Getter;
import lombok.Setter;
import com.elevatemc.elib.border.Border;
import com.elevatemc.elib.border.event.border.BorderEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

public class PlayerBorderEvent extends BorderEvent implements Cancellable {

    @Getter private Player player;
    @Getter @Setter private boolean cancelled;

    public PlayerBorderEvent(Border border, Player player) {
        super(border);
        this.player = player;
    }

}

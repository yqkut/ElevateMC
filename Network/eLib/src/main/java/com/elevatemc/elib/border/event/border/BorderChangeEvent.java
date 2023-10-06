package com.elevatemc.elib.border.event.border;

import com.elevatemc.elib.border.runnable.BorderTask;
import lombok.Getter;
import com.elevatemc.elib.border.Border;
import com.elevatemc.elib.cuboid.Cuboid;

public class BorderChangeEvent extends BorderEvent {

    @Getter private int previousSize;
    @Getter private Cuboid previousBounds;
    @Getter private BorderTask.BorderAction action;

    public BorderChangeEvent(Border border, int previousSize, Cuboid previousBounds, BorderTask.BorderAction action) {
        super(border);
        this.previousSize = previousSize;
        this.previousBounds = previousBounds;
        this.action = action;
    }

}


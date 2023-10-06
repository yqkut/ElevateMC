package com.elevatemc.elib.border.runnable;

import com.elevatemc.elib.eLib;
import com.elevatemc.elib.border.event.border.BorderChangeEvent;
import lombok.Getter;
import com.elevatemc.elib.border.Border;
import com.elevatemc.elib.cuboid.Cuboid;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.TimeUnit;

public class BorderTask extends BukkitRunnable {

    @Getter private Border border;
    @Getter private int borderChange = 5;
    @Getter private long borderChangeDelay = 10L;
    @Getter private BorderAction action;
    @Getter private boolean first;
    @Getter private int tracker;

    public BorderTask(Border border) {
        this.action = BorderAction.NONE;
        this.first = true;
        this.tracker = 0;
        this.border = border;
        this.runTaskTimer(eLib.getInstance(), 1L, 1L);
    }

    public void run() {
        if (this.action != BorderAction.NONE) {
            ++this.tracker;
        }

        if (this.border != null && this.tracker % 20 == 0) {

            int seconds = this.tracker / 20;

            if ((long)seconds % this.borderChangeDelay == 0L) {

                int previousSize = this.border.getSize();

                Cuboid previous = null;

                for (BorderAction borderAction : BorderAction.values()) {

                    switch (borderAction) {

                        case SHRINK: {
                            previous = this.border.contract(this.borderChange);
                            break;
                        }

                        case GROW: {
                            previous = this.border.expand(this.borderChange);
                            break;
                        }

                        case NONE: {

                        }

                        default: {
                            return;
                        }
                    }

                }

                eLib.getInstance().getServer().getPluginManager().callEvent(new BorderChangeEvent(this.border, previousSize, previous, this.action));
                this.border.fill();
            }
        }

    }

    public BorderTask setAction(BorderAction action) {
        this.action = action;
        this.tracker = 0;
        return this;
    }

    public BorderTask setBorderChangeDelay(long time, TimeUnit timeUnit) {
        this.borderChangeDelay = timeUnit.toSeconds(time);
        this.tracker = 0;
        return this;
    }

    public BorderTask setBorderChange(int borderChange) {
        this.borderChange = borderChange;
        return this;
    }

    public enum BorderAction {

        SHRINK,
        GROW,
        SET,
        NONE

    }
}

package com.elevatemc.elib.autoreboot;

import com.elevatemc.elib.eLib;
import com.elevatemc.elib.autoreboot.task.ServerRebootTask;
import lombok.Getter;

public class AutoRebootHandler {

    @Getter private ServerRebootTask serverRebootTask = null;

    public AutoRebootHandler() {
        eLib.getInstance().getCommandHandler().registerPackage(eLib.getInstance(),"com.elevatemc.elib.autoreboot.command");
    }

    public void rebootServer(long time) {

        if (this.serverRebootTask != null) {
            throw new IllegalStateException("A reboot is already in progress.");
        }

        this.serverRebootTask = new ServerRebootTask(time);
        this.serverRebootTask.runTaskTimer(eLib.getInstance(),20L,20L);
    }

    public boolean isRebooting() {
        return this.serverRebootTask != null;
    }

    public int getRebootSecondsRemaining() {
        return this.serverRebootTask == null ? -1 : this.serverRebootTask.getSecondsRemaining();
    }

    public void cancelReboot() {

        if (this.serverRebootTask == null) {
            return;
        }

        this.serverRebootTask.cancel();
        this.serverRebootTask = null;
    }

}

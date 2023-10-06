package com.elevatemc.potpvp.util;

/**
 * Copyright (c) 2022 - Tranquil, LLC.
 *
 * @author ImHacking
 * @date 6/6/2022
 */
public class Cooldown {
    Long endTime;

    public Cooldown(long time) {
        this.endTime = System.currentTimeMillis() + time;
    }

    public long getRemaining () {
        return this.endTime - System.currentTimeMillis();
    }

    public boolean hasExpired () {
        if (this.endTime < System.currentTimeMillis()) {
            return true;
        } else {
            return false;
        }
    }
}

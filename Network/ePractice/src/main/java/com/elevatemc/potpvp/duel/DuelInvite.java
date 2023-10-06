package com.elevatemc.potpvp.duel;

import com.google.common.base.Preconditions;
import lombok.Getter;
import com.elevatemc.potpvp.gamemode.GameMode;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public abstract class DuelInvite<T> {

    @Getter private final T sender;
    @Getter private final T target;
    @Getter private final GameMode gameMode;
    @Getter private final String arenaName;
    @Getter private final Instant timeSent;

    public DuelInvite(T sender, T target, GameMode gameMode, String arenaName) {
        this.sender = Preconditions.checkNotNull(sender, "sender");
        this.target = Preconditions.checkNotNull(target, "target");
        this.gameMode = Preconditions.checkNotNull(gameMode, "gameMode");
        this.arenaName = arenaName; // Arena can be null -> random arena
        this.timeSent = Instant.now();
    }

    public boolean isExpired() {
        long sentAgo = ChronoUnit.SECONDS.between(timeSent, Instant.now());
        return sentAgo > DuelHandler.DUEL_INVITE_TIMEOUT_SECONDS;
    }

}
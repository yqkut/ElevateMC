package com.elevatemc.potpvp.match.rematch;

import com.elevatemc.potpvp.gamemode.GameMode;
import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@ToString
public final class RematchData {

    @Getter private final UUID sender;
    @Getter private final UUID target;
    @Getter private final GameMode gameMode;
    @Getter private final Instant expiresAt;
    @Getter private final String arenaName;

    RematchData(UUID sender, UUID target, GameMode gameMode, int durationSeconds, String arenaName) {
        this.sender = Preconditions.checkNotNull(sender, "sender");
        this.target = Preconditions.checkNotNull(target, "target");
        this.gameMode = Preconditions.checkNotNull(gameMode, "gameMode");
        this.expiresAt = Instant.now().plusSeconds(durationSeconds);
        this.arenaName = arenaName; // Null arena -> random arena
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    public int getSecondsUntilExpiration() {
        return (int) ChronoUnit.SECONDS.between(Instant.now(), expiresAt);
    }

}
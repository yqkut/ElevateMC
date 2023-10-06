package com.elevatemc.potpvp.hctranked.game;

import com.elevatemc.potpvp.pvpclasses.PvPClasses;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

public class RankedGameTeam {
    /**
     * All players who were ever part of this team, including those who logged off / died
     */
    @Getter
    private final Set<UUID> players;

    @Getter
    private final Set<UUID> joinedPlayers;

    @Getter
    private final UUID captain;

    @Getter @Setter
    private boolean ready;

    @Getter
    private Map<UUID, PvPClasses> kits = new HashMap<>();

    // convenience constructor for 1v1s, queues, etc
    public RankedGameTeam(Set<UUID> players, UUID captain) {
        this.players = players;
        this.captain = captain;
        this.joinedPlayers = new HashSet<>();
        this.ready = false;
    }
}

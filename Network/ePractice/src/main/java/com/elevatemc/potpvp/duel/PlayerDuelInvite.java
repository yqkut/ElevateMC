package com.elevatemc.potpvp.duel;

import com.elevatemc.potpvp.gamemode.GameMode;
import org.bukkit.entity.Player;

import java.util.UUID;

public final class PlayerDuelInvite extends DuelInvite<UUID> {

    public PlayerDuelInvite(Player sender, Player target, GameMode gameMode, String arenaName) {
        super(sender.getUniqueId(), target.getUniqueId(), gameMode, arenaName);
    }

}
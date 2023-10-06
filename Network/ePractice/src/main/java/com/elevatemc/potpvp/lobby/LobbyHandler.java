package com.elevatemc.potpvp.lobby;

import com.elevatemc.elib.eLib;
import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.follow.FollowHandler;
import com.elevatemc.potpvp.follow.command.UnfollowCommand;
import com.elevatemc.potpvp.lobby.listener.LobbyGeneralListener;
import com.elevatemc.potpvp.lobby.listener.LobbyItemListener;
import com.elevatemc.potpvp.lobby.listener.LobbyParkourListener;
import com.elevatemc.potpvp.lobby.listener.LobbySpecModeListener;
import com.elevatemc.potpvp.util.InventoryUtils;
import com.elevatemc.potpvp.util.PatchedPlayerUtils;
import com.elevatemc.potpvp.util.VisibilityUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.*;

public final class LobbyHandler {

    /**
     * Stores players who are in "spectator mode", which gives them fly mode
     * and a different lobby hotbar. This setting is purely cosmetic, it doesn't
     * change what a player can/can't do (with the exception of not giving them
     * certain clickable items - but that's just a UX decision)
     */
    private final Set<UUID> spectatorMode = new HashSet<>();
    private final Map<UUID, Long> returnedToLobby = new HashMap<>();

    public LobbyHandler() {
        Bukkit.getPluginManager().registerEvents(new LobbyGeneralListener(this), PotPvPSI.getInstance());
        Bukkit.getPluginManager().registerEvents(new LobbyItemListener(this), PotPvPSI.getInstance());
        Bukkit.getPluginManager().registerEvents(new LobbySpecModeListener(), PotPvPSI.getInstance());
        Bukkit.getPluginManager().registerEvents(new LobbyParkourListener(), PotPvPSI.getInstance());
    }

    /**
     * Returns a player to the main lobby. This includes performing
     * the teleport, clearing their inventory, updating their nametag,
     * etc. etc.
     * @param player the player who is to be returned
     */
    public void returnToLobby(Player player) {
        returnToLobbySkipItemSlot(player);
        player.getInventory().setHeldItemSlot(0);
    }

    private void returnToLobbySkipItemSlot(Player player) {
        player.teleport(getLobbyLocation());

        eLib.getInstance().getNameTagHandler().reloadPlayer(player);
        eLib.getInstance().getNameTagHandler().reloadOthersFor(player);

        VisibilityUtils.updateVisibility(player);
        PatchedPlayerUtils.resetInventory(player, GameMode.SURVIVAL, true);
        InventoryUtils.resetInventoryDelayed(player);

        for(PotionEffect effect : player.getActivePotionEffects())
        {
            player.removePotionEffect(effect.getType());
        }

        player.setGameMode(GameMode.SURVIVAL);

        if(player.hasPermission("potpvp.lobby.flight")) {
            player.setAllowFlight(true);
        }

        returnedToLobby.put(player.getUniqueId(), System.currentTimeMillis());
    }

    public long getLastLobbyTime(Player player) {
        return returnedToLobby.getOrDefault(player.getUniqueId(), 0L);
    }

    public boolean isInLobby(Player player) {
        return !PotPvPSI.getInstance().getMatchHandler().isPlayingOrSpectatingMatch(player);
    }

    public boolean isInSpectatorMode(Player player) {
        return spectatorMode.contains(player.getUniqueId());
    }

    public void setSpectatorMode(Player player, boolean mode) {
        boolean changed;

        if (mode) {
            changed = spectatorMode.add(player.getUniqueId());
        } else {
            FollowHandler followHandler = PotPvPSI.getInstance().getFollowHandler();
            followHandler.getFollowing(player).ifPresent(i -> UnfollowCommand.unfollow(player));

            changed = spectatorMode.remove(player.getUniqueId());
        }

        if (changed) {
            InventoryUtils.resetInventoryNow(player);

            if (!mode) {
                returnToLobbySkipItemSlot(player);
            }
        }
    }

    public Location getLobbyLocation() {
        Location spawn = Bukkit.getWorlds().get(0).getSpawnLocation();
        spawn.add(0.5, 0, 0.5); // 'prettify' so players spawn in middle of block
        return spawn;
    }

}
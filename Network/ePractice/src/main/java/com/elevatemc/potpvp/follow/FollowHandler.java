package com.elevatemc.potpvp.follow;


import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.follow.listener.FollowGeneralListener;
import com.elevatemc.potpvp.match.Match;
import com.elevatemc.potpvp.match.MatchHandler;
import com.elevatemc.potpvp.match.MatchState;
import com.elevatemc.potpvp.util.InventoryUtils;
import com.elevatemc.potpvp.util.VisibilityUtils;
import com.elevatemc.elib.eLib;
import com.elevatemc.elib.util.UUIDUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class FollowHandler {

    // (follower -> target)
    private final Map<UUID, UUID> followingData = new ConcurrentHashMap<>();

    public FollowHandler() {
        Bukkit.getPluginManager().registerEvents(new FollowGeneralListener(this), PotPvPSI.getInstance());
    }

    public Optional<UUID> getFollowing(Player player) {
        return Optional.ofNullable(followingData.get(player.getUniqueId()));
    }

    public void startFollowing(Player player, Player target) {
        followingData.put(player.getUniqueId(), target.getUniqueId());
        player.sendMessage(ChatColor.YELLOW + "You are now following " + ChatColor.GOLD + target.getName() + ChatColor.YELLOW + ".");

        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        Match targetMatch = matchHandler.getMatchPlayingOrSpectating(target);

        if (targetMatch != null && targetMatch.getState() != MatchState.ENDING) {
            targetMatch.addSpectator(player, target);
        } else {
            InventoryUtils.resetInventoryDelayed(player);
            VisibilityUtils.updateVisibility(player);
            eLib.getInstance().getNameTagHandler().reloadOthersFor(player);

            player.teleport(target);
        }
    }

    public void stopFollowing(Player player) {
        UUID prevTarget = followingData.remove(player.getUniqueId());

        if (prevTarget != null) {
            player.sendMessage(ChatColor.YELLOW + "You are no longer following " + ChatColor.GOLD + UUIDUtils.name(prevTarget) + ChatColor.YELLOW + ".");
            InventoryUtils.resetInventoryDelayed(player);
            VisibilityUtils.updateVisibility(player);
            eLib.getInstance().getNameTagHandler().reloadOthersFor(player);
        }
    }

    public Set<UUID> getFollowers(Player player) {
        Set<UUID> followers = new HashSet<>();

        followingData.forEach((follower, followed) -> {
            if (followed == player.getUniqueId()) {
                followers.add(follower);
            }
        });

        return followers;
    }

}
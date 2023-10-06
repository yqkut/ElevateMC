package com.elevatemc.potpvp.match;

import com.elevatemc.elib.eLib;
import com.google.common.collect.ImmutableSet;
import com.lunarclient.bukkitapi.LunarClientAPI;
import com.lunarclient.bukkitapi.object.LCWaypoint;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Represents one team participating in a {@link Match}
 */
public final class MatchTeam {

    /**
     * All players who were ever part of this team, including those who logged off / died
     */
    @Getter private final Set<UUID> allMembers;

    /**
     * All players who are currently alive.
     */
    private final Set<UUID> aliveMembers = Collections.newSetFromMap(new ConcurrentHashMap<>());

    @Getter @Setter private int hits = 0;

    /**
     * This is used for gamemodes with lives
     */
    @Getter private int lives = 0;

    @Getter private UUID focus = null;

    // convenience constructor for 1v1s, queues, etc
    public MatchTeam(UUID initialMember) {
        this(ImmutableSet.of(initialMember));
    }


    public MatchTeam(Collection<UUID> initialMembers) {
        this.allMembers = ImmutableSet.copyOf(initialMembers);
        this.aliveMembers.addAll(initialMembers);
    }

    /**
     * Marks the given player as dead (will no longer appear in {@link MatchTeam#getAliveMembers()}, etc)
     * @param playerUuid the player to mark as dead
     */
    void markDead(UUID playerUuid) {
        aliveMembers.remove(playerUuid);
    }

    /**
     * Checks if the given player is still alive (shorthand for .getAliveMembers().contains())
     * @param playerUuid the player to check
     * @return if the given player is still alive
     */
    public boolean isAlive(UUID playerUuid) {
        return aliveMembers.contains(playerUuid);
    }

    /**
     * Gets a immutable set of all alive team members
     * @see MatchTeam#aliveMembers
     * @return immutable set of all alive team members
     */
    public Set<UUID> getAliveMembers() {
        return ImmutableSet.copyOf(aliveMembers);
    }

    public UUID getFirstAliveMember() {
        return aliveMembers.iterator().next();
    }

    public UUID getFirstMember() {
        return allMembers.iterator().next();
    }

    /**
     * Sends a basic chat message to all alive members
     * @see MatchTeam#aliveMembers
     * @param message the message to send
     */
    public void messageAlive(String message) {
        forEachAlive(p -> p.sendMessage(message));
    }

    /**
     * Plays a sound for all alive members
     * @param sound the Sound to play
     * @param pitch the pitch to play the provided sound at
     */
    public void playSoundAlive(Sound sound, float pitch) {
        forEachAlive(p -> p.playSound(p.getLocation(), sound, 10F, pitch));
    }

    /**
     * Sends a rally waypoint to all alive members
     * @param from the player that made the rally
     */
    public void sendRally(Player from) {
        forEachAlive(p -> {
            p.sendMessage(ChatColor.DARK_AQUA + "⚐ " + from.getName() + ChatColor.AQUA + " has updated the rally point!");
            removeRallyForPlayer(p);
            LunarClientAPI.getInstance().sendWaypoint(p, new LCWaypoint("Rally", from.getLocation(), 1, true, true));
        });
    }

    public void removeRally() {
        forEachMembers(this::removeRallyForPlayer);
    }

    public void removeRallyForPlayer(Player player) {
        LunarClientAPI.getInstance().removeWaypoint(player, new LCWaypoint("Rally", player.getLocation(), 1, true, true));
    }

    public void setFocus(Player target) {
        Player oldPlayer = Bukkit.getPlayer(focus);
        if (target != null) {
            focus = target.getUniqueId();
            eLib.getInstance().getNameTagHandler().reloadPlayer(target);
        } else {
            focus = null;
        }
        if (oldPlayer != null) eLib.getInstance().getNameTagHandler().reloadPlayer(oldPlayer);
    }

    public void setLives(int lives) {
        this.lives = lives;
    }

    public void forEachAlive(Consumer<Player> consumer) {
        for (UUID member : aliveMembers) {
            Player memberBukkit = Bukkit.getPlayer(member);

            if (memberBukkit != null) {
                consumer.accept(memberBukkit);
            }
        }
    }

    public void forEachMembers(Consumer<Player> consumer) {
        for (UUID member : allMembers) {
            Player memberBukkit = Bukkit.getPlayer(member);

            if (memberBukkit != null) {
                consumer.accept(memberBukkit);
            }
        }
    }

}
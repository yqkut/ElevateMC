package com.elevatemc.elib.fake;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;

/**
 * @author ImHacking
 */
public interface FakeEntity {

    boolean show(Player player);

    boolean showToAll();

    boolean hide(Player player);

    boolean hideFromAll();

    void teleport(Location location);

    boolean isShownToPlayer(UUID uuid);

    String getName();

    int getId();

    int getEntityId();

    UUID getUUID();

    void addToTeam(Player player);

    void handleDisconnect(UUID uuid);

    Set<UUID> getCurrentlyViewing();

    Set<UUID> getShouldBeAbleToView();

    World getWorld();

    Location getLocation();

    default int range() {
        return 1024;
    }

    ;

    default boolean showOnJoin() {
        return true;
    }

    ;

}

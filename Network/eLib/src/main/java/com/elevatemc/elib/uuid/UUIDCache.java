package com.elevatemc.elib.uuid;

import com.elevatemc.elib.eLib;
import com.elevatemc.elib.uuid.impl.redis.RedisUUIDCache;
import com.elevatemc.elib.uuid.impl.IUUIDCache;
import com.elevatemc.elib.uuid.listener.UUIDListener;
import lombok.Getter;
import org.bukkit.ChatColor;

import java.util.*;

public final class UUIDCache implements IUUIDCache {

    public static final UUID CONSOLE_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");
    public static final String UPDATE_PREFIX = ChatColor.BLUE + "[UUIDCache]";

    public static final  Map<UUID,Boolean> MONITOR_CACHE = new HashMap<>();

    @Getter private IUUIDCache impl;

    public UUIDCache() {

        try {
            this.impl = new RedisUUIDCache();
        } catch (Exception e) {
            e.printStackTrace();
        }

        eLib.getInstance().getServer().getPluginManager().registerEvents(new UUIDListener(), eLib.getInstance());

        this.update(CONSOLE_UUID,"CONSOLE");
    }

    public UUID uuid(String name) {
        return this.impl.uuid(name);
    }

    public String name(UUID uuid) {
        return this.impl.name(uuid);
    }

    public boolean cached(UUID uuid) {
        return this.impl.cached(uuid);
    }

    public boolean cached(String name) {
        return this.impl.cached(name);
    }

    public void ensure(UUID uuid) {
        this.impl.ensure(uuid);
    }

    public void update(UUID uuid, String name) {
        this.impl.update(uuid, name);
    }

    public void updateAll(UUID uuid,String name) {
        this.impl.updateAll(uuid,name);
    }

    public void monitor(String message) {

    }
}
package dev.apposed.prime.spigot.module.profile.permission;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import dev.apposed.prime.spigot.Prime;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

public class PermissionHandler {
    private static Field permissionsField;
    private final Map<UUID, PermissionAttachment> attachments = new ConcurrentHashMap<UUID, PermissionAttachment>();

    public void update(Player player, Map<String, Boolean> permissions) {
        this.attachments.computeIfAbsent(player.getUniqueId(), i -> player.addAttachment(Prime.getInstance()));
        PermissionAttachment attachment = this.attachments.get(player.getUniqueId());
        try {
            permissionsField.set(attachment, permissions);
            player.recalculatePermissions();
        }
        catch (IllegalAccessException ex) {
            ex.printStackTrace();
        }
    }

    public void removeAttachment(Player player) {
        this.attachments.remove(player.getUniqueId());
    }

    static {
        try {
            permissionsField = PermissionAttachment.class.getDeclaredField("permissions");
            permissionsField.setAccessible(true);
        }
        catch (NoSuchFieldException ex) {
            ex.printStackTrace();
        }
    }
}
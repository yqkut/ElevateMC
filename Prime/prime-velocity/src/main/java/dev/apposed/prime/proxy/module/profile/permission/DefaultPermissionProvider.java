package dev.apposed.prime.proxy.module.profile.permission;

import com.velocitypowered.api.permission.PermissionFunction;
import com.velocitypowered.api.permission.PermissionProvider;
import com.velocitypowered.api.permission.PermissionSubject;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.velocitypowered.api.proxy.Player;

public final class DefaultPermissionProvider implements PermissionProvider {

    public static final DefaultPermissionProvider INSTANCE = new DefaultPermissionProvider();

    private DefaultPermissionProvider() {
    }

    @Override
    public PermissionFunction createFunction(PermissionSubject permissionSubject) {
        if (permissionSubject instanceof Player) {
            final Player player = (Player) permissionSubject;
            return new DefaultPermissionFunction(player);
        }

        if (permissionSubject instanceof ConsoleCommandSource) {
            return PermissionFunction.ALWAYS_TRUE;
        }

        throw new RuntimeException("Unable to create permission function for unknown type " + permissionSubject.getClass().getName());
    }
}

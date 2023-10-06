package dev.apposed.prime.proxy.module.profile.permission;

import com.velocitypowered.api.permission.PermissionFunction;
import com.velocitypowered.api.permission.Tristate;
import com.velocitypowered.api.proxy.Player;
import dev.apposed.prime.proxy.PrimeProxy;
import dev.apposed.prime.proxy.module.profile.Profile;
import dev.apposed.prime.proxy.module.profile.ProfileHandler;

import java.util.Optional;
import java.util.UUID;

public class DefaultPermissionFunction implements PermissionFunction {

    private final UUID uuid;

    DefaultPermissionFunction(Player player) {
        this.uuid = player.getUniqueId();
    }

    @Override
    public Tristate getPermissionValue(String s) {
        if (s == null) {
            return Tristate.FALSE;
        }

        Optional<Profile> profileOpt = PrimeProxy.getInstance().getModuleHandler().getModule(ProfileHandler.class).getProfile(uuid);;
        if (profileOpt.isPresent()) {
            return profileOpt.get().hasPermission(s) ? Tristate.TRUE : Tristate.FALSE;
        } else {
            return Tristate.UNDEFINED;
        }
    }
}
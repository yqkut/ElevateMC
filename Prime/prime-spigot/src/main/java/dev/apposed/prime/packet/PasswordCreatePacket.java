package dev.apposed.prime.packet;

import dev.apposed.prime.spigot.Prime;
import dev.apposed.prime.spigot.module.database.redis.packet.Packet;
import dev.apposed.prime.spigot.module.profile.Profile;
import dev.apposed.prime.spigot.module.profile.ProfileHandler;
import lombok.AllArgsConstructor;

import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
public class PasswordCreatePacket extends Packet {

    private final String uuid;
    private final String password;

    @Override
    public void onReceive() {
        final ProfileHandler profileHandler = Prime.getInstance().getModuleHandler().getModule(ProfileHandler.class);
        final Optional<Profile> profileOptional = profileHandler.getProfile(UUID.fromString(uuid));
        if(!profileOptional.isPresent()) return;
        final Profile profile = profileOptional.get();
        profile.setPassword(password);
        profileHandler.sendSync(profile);
    }

    @Override
    public void onSend() {

    }
}

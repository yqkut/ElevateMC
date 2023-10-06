package dev.apposed.prime.packet;

import dev.apposed.prime.packet.type.RefreshType;
import dev.apposed.prime.spigot.Prime;
import dev.apposed.prime.spigot.module.database.redis.packet.Packet;
import dev.apposed.prime.spigot.module.profile.Profile;
import dev.apposed.prime.spigot.module.profile.ProfileHandler;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

@AllArgsConstructor
public class ProfileRefreshPacket extends Packet {

    private Profile profile;
    private RefreshType type;

    @Override
    public void onSend() {
    }

    @Override
    public void onReceive() {
        final ProfileHandler profileHandler = Prime.getInstance().getModuleHandler().getModule(ProfileHandler.class);
        switch(type) {
            case UPDATE: {
                profileHandler.updateProfile(profile);
                break;
            }
            case REMOVE: {
                profileHandler.getProfiles().remove(profile);
                break;
            }
        }
    }
}

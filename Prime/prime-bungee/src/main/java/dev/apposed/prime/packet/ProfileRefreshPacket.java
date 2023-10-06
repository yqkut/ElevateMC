package dev.apposed.prime.packet;

import dev.apposed.prime.packet.type.RefreshType;
import dev.apposed.prime.proxy.PrimeProxy;
import dev.apposed.prime.proxy.module.database.redis.packet.Packet;
import dev.apposed.prime.proxy.module.profile.Profile;
import dev.apposed.prime.proxy.module.profile.ProfileHandler;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ProfileRefreshPacket extends Packet {

    private Profile profile;
    private RefreshType type;

    @Override
    public void onSend() {
    }

    @Override
    public void onReceive() {
        final ProfileHandler profileHandler = PrimeProxy.getInstance().getModuleHandler().getModule(ProfileHandler.class);
        switch(type) {
            case UPDATE: {
                profileHandler.updateProfile(profile);
                break;
            }
            case REMOVE: {
                // do nothing, no point
                break;
            }
        }
    }
}
